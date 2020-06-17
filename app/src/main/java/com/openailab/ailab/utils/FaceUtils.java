package com.openailab.ailab.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.openailab.ailab.DaoManager;
import com.openailab.ailab.DaoSession;
import com.openailab.ailab.dao.UserInfos;
import com.openailab.ailab.function.Consumer;
import com.openailab.ailab.serialport.MyFunc;
import com.openailab.ailab.serialport.SerialHelper;
import com.openailab.ailab.serialport.bean.ComBean;
import com.openailab.ailab.ui.ReadCardActivity;
import com.openailab.facelibrary.FaceAPP;
import com.openailab.facelibrary.FaceAPP.Image;
import com.openailab.facelibrary.FaceInfo;
import com.openailab.ailab.widget.RectView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * @author ZyElite
 */
public class FaceUtils {

    private Speaker mSpeaker;
    private int matW = 1080;
    private int matH = 1280;

    static final public int STATE_NO_DETECT = 0;
    static final public int STATE_DETECT = 1;
    static final public int STATE_NORMAL = 2;
    static final public int STATE_ABNORMAL = 3;
    static final public int STATE_MASKS = 4;


    private Mat srcMatR = new Mat(matW, matH, CvType.CV_8UC1);
    private Mat matR = new Mat(CameraUtils.DEFAULT_WIDTH, CameraUtils.DEFAULT_HEIGHT, CvType.CV_8UC3);

    private FaceAPP mFace = FaceAPP.getInstance();

    private int[] result = new int[1];

    private ArrayList<FaceInfo> mFaceInfos = new ArrayList<>();

    private static volatile FaceUtils instance;
    private volatile SerialHelper serialHelper;
    private ComBean mComBean;

    private Consumer mStateClickListener;

    private boolean mInformation = false;


    private FaceUtils() {

        serialHelper = new SerialHelper("/dev/ttyS3", "115200") {
            @Override
            protected void onDataReceived(ComBean comRecData) {
                mComBean = comRecData;
            }
        };

        try {
            serialHelper.open();
            serialHelper.setHexLoopData("FE322A00");
            serialHelper.startSend();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FaceUtils getInstance() {
        synchronized (FaceUtils.class) {
            if (instance == null) {
                instance = new FaceUtils();
            }
        }
        return instance;
    }


    public void openRGB(Camera camera) {
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                srcMatR.put(0, 0, data);
            }
        });
    }


    public void setInformation(boolean mInformation) {
        this.mInformation = mInformation;
    }

    public void setSpeaker(Speaker speaker) {
        this.mSpeaker = speaker;
    }

    public Speaker getSpeaker() {
        return this.mSpeaker;
    }

    public void detectQRCode() {
        Bitmap bitmap = Bitmap.createBitmap(srcMatR.width(), srcMatR.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcMatR, bitmap);
        Result result = com.openailab.ailab.utils.Utils.decodeQR(bitmap);
        if (result != null) {
            //TODO 判断二维码是否有效并将温度写入数据库
            mStateClickListener.accept(STATE_NO_DETECT, "wear");
            mSpeaker.speak("记录成功，请通行");
        }

    }

    public void detect(Context context, RectView mRectView) {
        if (!srcMatR.empty() && !mInformation) {
            Imgproc.cvtColor(srcMatR, matR, Imgproc.COLOR_YUV2RGBA_NV12, 4);
            Core.transpose(matR, matR);
            //            Core.flip(matR, matR, 0);
            //            Imgcodecs.imwrite("/sdcard/openailab/matR.jpg", matR);
            Image image = FaceAPP.getInstance().new Image();
            image.matAddrframe = matR.getNativeObjAddr();
            int detect = mFace.Detect(image, mFaceInfos, result);
            if (detect == 0 && mFaceInfos.size() != 0) {
                serialHelper.sendHex("FE322A00");
                float isWearingMask = mFace.IsWearingMask(image, mFaceInfos.get(0));
                if (mComBean == null) {
                    return;
                }
                String temperature = MyFunc.ByteArrToHex(mComBean.bRec);
                if (temperature.isEmpty()) {
                    return;
                }
                Float valueOf = Float.valueOf(temperature);
                temperature = String.format("%.1f", valueOf);
                boolean isMasks = isWearingMask == 1;
                if (isMasks) {
                    mRectView.setTemperature(temperature);
                    drawFrame(mRectView, mFaceInfos.get(0), mRectView.getWidth(), mRectView.getHeight());
                    if (valueOf > 35.5 && valueOf <= 37.5) {
                        /*比对方案*/

                        float[] feature = mFace.GetFeature(image.matAddrframe);
                        float[] score = new float[1];
                        if (feature != null) {
                            String name = mFace.QueryDB(feature, score);
                            Log.i(TAG, "detect: name = " + name + " score = " + score[0]);
                            if (score[0] < 0.7f) {
                                Bundle bundle = new Bundle();
                                bundle.putFloat("guest_heat", valueOf);
                                Intent intent = new Intent(context, ReadCardActivity.class);
                                intent.putExtra("face_feature", feature);
                                intent.putExtras(bundle);

                                context.startActivity(intent);
                                mSpeaker.speak("新朋友，请刷身份证注册");
                                Log.i(TAG, "valueOf   = " + valueOf);
                                Log.i(TAG, "face_feature   = " + feature);
                                Log.i(TAG, "face_feature   = " + feature.length);
                                //                                init(feature);

                            } else {
                                mStateClickListener.accept(STATE_NORMAL, temperature);
                                mRectView.setState(STATE_NORMAL);
                                mSpeaker.speak("体温正常, 请通行");
                                DaoSession daoSession = DaoManager.getDaoSession();
                                UserInfos userInfos = new UserInfos(name, "无", "暂无",
                                        "男", getNowDate(), temperature);

                                daoSession.insertOrReplace(userInfos);

                            }
                        }

                        /*比对方案 END*/
                        //                        mStateClickListener.accept(STATE_NORMAL,"wearMask");
                        //                        mSpeaker.speak("体温正常, 请出示您的健康码");


                    } else if (valueOf > 35 && valueOf <= 36 || valueOf > 37.5) {
                        mStateClickListener.accept(STATE_ABNORMAL, "wearMask");
                        mRectView.setState(STATE_ABNORMAL);
                        mSpeaker.speak("体温异常");
                    } else if (valueOf <= 35) {
                        mStateClickListener.accept(STATE_NO_DETECT, "wearMask");
                        mRectView.clearRect();
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    mStateClickListener.accept(STATE_MASKS, "0");
                    mRectView.setState(STATE_MASKS);
                    mRectView.setTemperature("");
                    drawFrame(mRectView, mFaceInfos.get(0), mRectView.getWidth(), mRectView.getHeight());
                    mSpeaker.speak("未戴口罩");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mStateClickListener.accept(STATE_NO_DETECT, "0");
                mRectView.clearRect();
            }
        } else {
            mRectView.clearRect();
        }
    }

    public void setStateClickListener(Consumer stateClickListener) {
        this.mStateClickListener = stateClickListener;
    }

    public void drawFrame(RectView rectView, FaceInfo faceInfo, int width, int height) {
        rectView.drawFaceRect(new Rect(
                faceInfo.mRect.left * width / CameraUtils.DEFAULT_HEIGHT,
                faceInfo.mRect.top * height / CameraUtils.DEFAULT_WIDTH,
                faceInfo.mRect.right * width / CameraUtils.DEFAULT_HEIGHT,
                faceInfo.mRect.bottom * height / CameraUtils.DEFAULT_WIDTH
        ));
    }

    public static void init(float[] feature) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < feature.length; i++) {
            stringBuilder.append(feature[i] + " ");
        }
        String s = stringBuilder.toString();
        Log.d(TAG, "feature.ts===" + s);
        System.out.println("init:" + s);

        //        toBack(stringBuilder.toString());
    }

    public static void toBack(String string) {
        String[] strings = string.split(" ");
        float[] fs = new float[strings.length];
        for (int i = 0; i < strings.length; i++) {
            fs[i] = Float.parseFloat(strings[i]);
        }

        System.out.println("toBack:" + Arrays.toString(fs));
        for (float f : fs) {
            System.out.println(f);
        }
    }

    private String getNowDate() {
        Date date = new Date();

        String time = date.toLocaleString();

        Log.i("md", "时间time为： " + time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分 E");

        String sim = dateFormat.format(date);


        Log.i("md", "时间sim为： " + sim);
        return sim;
    }
}
