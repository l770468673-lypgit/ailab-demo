package com.openailab.ailab.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.openailab.ailab.AiLabApplication;
import com.openailab.ailab.R;
import com.openailab.ailab.utils.Speaker;
import com.openailab.ailab.utils.Utils;
import com.openailab.ailab.widget.IDCardDialog;
import com.openailab.ailab.widget.InfoDialog;
import com.openailab.ailab.widget.RectView;
import com.openailab.facelibrary.FaceAPP;
import com.openailab.ailab.utils.CameraUtils;
import com.openailab.ailab.utils.FaceUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author ZyElite
 */
public class FullscreenActivity extends AppCompatActivity {
    private static final String TAG = "FullscreenActivity";
    private FaceAPP mFace = FaceAPP.getInstance();
    private TextView mTime;
    private boolean isEnableFaceDetect = false;
    private boolean isEnableQRDetect = false;
    private Context mContext;
    private Thread faceDetectThread = new Thread(){
        @Override
        public void run() {
            while (true) {
                if(isEnableFaceDetect){
                    FaceUtils.getInstance().detect(mContext, mRectView);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Thread qrCodeDetectThread = new Thread(){
        @Override
        public void run() {
            super.run();
            while(true) {
                if (isEnableQRDetect) {
                    FaceUtils.getInstance().detectQRCode();

                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private Handler mHandler = new Handler();

    private TextView mTvCompanyName;
    private ImageView mTopbg;
    private ImageView mBottomBg;
    private TextView mWelcome;
    private RectView mRectView;
    private Group mInfo;
    private ConstraintLayout layoutQRFrame;

    private Speaker mSpeaker;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTime.setText(mFormat.format(System.currentTimeMillis()));
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private void handlerPostDelayed() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_fullscreen);
        AiLabApplication a = (AiLabApplication) getApplication();
        hideBottomUIMenu();
        initView();
        mSpeaker = new Speaker(this);
        FaceUtils.getInstance().setSpeaker(mSpeaker);
        initFaceService();
        faceDetectThread.start();
        //qrCodeDetectThread.start();button_register
        setFaceState();
        handlerPostDelayed();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }



    private void initView() {
        mTime = findViewById(R.id.time);
        mTopbg = findViewById(R.id.iv_top_bg);
        mBottomBg = findViewById(R.id.iv_bottom_bg);
        mWelcome = findViewById(R.id.tv_welcome);
        mRectView = findViewById(R.id.rectView);
        mInfo = findViewById(R.id.gInfo);
        mTvCompanyName = findViewById(R.id.tvCompanyName);
        layoutQRFrame = findViewById(R.id.layout_qrFrame);
    }


    private void initFaceService() {
        copyFilesAssets("openailab", "/mnt/sdcard/openailab");
        String oem_id = "2020031621650003";
        String contract_id = "04b2";
        String password = "15fcc24c4067eccd15fcc24c608e957d";
        String uidStr = oem_id + contract_id;
        int ret = mFace.AuthorizedDevice(uidStr, password, this);
        Log.i(TAG, "AuthorizedDevice: ret = " + ret);
        final String[] params = new String[]{
                "a", "b", "c", "d", "factor", "min_size", "clarity", "perfoptimize", "livenessdetect", "gray2colorScale", "frame_num", "quality_thresh", "mode", "facenum", "liveness_thresh", "iou_thresh", "mask_thresh"
        };
        final double[] value = new double[]{
                0.6, 0.7, 0.7, 0.63, 0.709, 40.0, 10.0, 0.0, 0.0, 0.5, 1.0, 0.8, 1.0, 1.0, 1.8, 0.05, 0.5
        };
        mFace.SetParameter(params, value);
        mFace.OpenDB();
    }

    private void startFaceService() {
        isEnableFaceDetect = true;
    }

    private void stopFaceService(){
        isEnableFaceDetect = false;
    }

    private void startQRServie(){
        isEnableQRDetect = true;
    }
    private void stopQRService(){
        isEnableQRDetect = false;
    }


    private void setFaceState() {
        FaceUtils.getInstance().setStateClickListener((state, value1) -> runOnUiThread(() -> {
            switch (state) {
                case FaceUtils.STATE_NO_DETECT:
                    mInfo.setVisibility(View.GONE);
                    mTopbg.setVisibility(View.VISIBLE);
                    mTvCompanyName.setVisibility(View.VISIBLE);
                    mBottomBg.setBackgroundResource(R.mipmap.ic_bottom_bg);
                    mTime.setVisibility(View.VISIBLE);
                    mWelcome.setText("欢迎光临，请刷脸通行");
                    mWelcome.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    layoutQRFrame.setVisibility(View.GONE);
                    break;
                case FaceUtils.STATE_DETECT:
                    mInfo.setVisibility(View.GONE);
                    mTopbg.setVisibility(View.VISIBLE);
                    mTvCompanyName.setVisibility(View.VISIBLE);
                    mBottomBg.setBackgroundResource(R.mipmap.ic_bottom_no_detect);
                    mTime.setVisibility(View.GONE);
                    mTime.setVisibility(View.VISIBLE);
                    mWelcome.setText("欢迎光临，请刷脸通行");
                    mWelcome.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    layoutQRFrame.setVisibility(View.GONE);
                    break;
                case FaceUtils.STATE_NORMAL:
                    stopFaceService();
                    startQRServie();
                    mTopbg.setVisibility(View.GONE);
                    mTvCompanyName.setVisibility(View.GONE);
                    mBottomBg.setBackgroundResource(R.mipmap.ic_bottom_normal);
                    mWelcome.setText("体温正常，请通行");
                    mTime.setVisibility(View.GONE);
                    mInfo.setVisibility(View.GONE);
                    mWelcome.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    //layoutQRFrame.setVisibility(View.VISIBLE);
                    QRCodeReader reader = new QRCodeReader();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startFaceService();
                            stopQRService();
                        }

                    }, 5000);
                    //mInfoDialog.show(getSupportFragmentManager(), "info");
                    break;
                case FaceUtils.STATE_ABNORMAL:
                    mInfo.setVisibility(View.GONE);
                    mTopbg.setVisibility(View.GONE);
                    mTvCompanyName.setVisibility(View.GONE);
                    mBottomBg.setBackgroundResource(R.mipmap.ic_bottom_abnormal);
                    mWelcome.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_warn, null), null, null, null);
                    mWelcome.setText("体温异常");
                    mTime.setVisibility(View.GONE);
                    layoutQRFrame.setVisibility(View.GONE);
                    break;
                case FaceUtils.STATE_MASKS:
                    mInfo.setVisibility(View.GONE);
                    mTopbg.setVisibility(View.GONE);
                    mTvCompanyName.setVisibility(View.GONE);
                    mBottomBg.setBackgroundResource(R.mipmap.ic_bottom_masks);
                    mWelcome.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_masks, null), null, null, null);
                    mWelcome.setText("请佩戴口罩");
                    mTime.setVisibility(View.GONE);
                    layoutQRFrame.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }));
    }


    private void copyFilesAssets(String oldPath, String newPath) {
        try {
            String[] fileNames = getAssets().list(oldPath);
            if (fileNames.length != 0) {
                File file = new File(newPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String fileName : fileNames) {
                    copyFilesAssets(oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                InputStream inputStream = getAssets().open(oldPath);
                FileOutputStream fileOutputStream = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byteCount);
                }
                fileOutputStream.flush();
                inputStream.close();
                fileOutputStream.close();
            }
        } catch (
                Exception e) {
        }

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        startFaceService();
        startQRServie();
//        if (!OpenCVLoader.initDebug()) {
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
//        } else {
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        stopFaceService();
        stopQRService();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
        }

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {
            super.onPackageInstall(operation, callback);
        }
    };


    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
