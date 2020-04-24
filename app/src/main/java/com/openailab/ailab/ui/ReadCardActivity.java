package com.openailab.ailab.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.openailab.ailab.R;
import com.openailab.ailab.utils.FaceUtils;
import com.openailab.facelibrary.FaceAPP;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Timer;
import java.util.TimerTask;

import static com.openailab.ailab.ui.CardInfoActivity.base64ToBitmap;

public class ReadCardActivity extends AppCompatActivity {
    private static final String TAG = "ReadCardActivity";
    private final String BROADCAST_READ_CARD_SUCCEED = "read_card_succeed";
    private final String BROADCAST_READ_CARD_FAILED = "read_card_failed";
    private final String READER_SERVICE_PACKAGE = "com.hoho.android.usbserial.examples";
    private final String READER_SERVICE_NAME = "com.hoho.android.usbserial.examples.CardReadService";

    private Timer timer = new Timer();
    private Intent readCardServiceIntent = new Intent();
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //            Bitmap bitmap = base64ToBitmap(intent.getStringExtra("strheadpic"));
            //            Mat mat = new Mat();
            //            Utils.bitmapToMat(bitmap, mat);
            //            FaceAPP.Image image= FaceAPP.getInstance().new Image();
            //            image.matAddrframe = mat.getNativeObjAddr();
            //            float[] feature = FaceAPP.getInstance().GetFeature(image.matAddrframe);
            //            if(feature != null) {
            //                float similarity = FaceAPP.getInstance().Compare(
            //                        getIntent().getFloatArrayExtra("face_feature"),
            //                        feature);
            //                Log.i(TAG, "onReceive: similarity = " + similarity);
            //                if (similarity > 0.1f) {
            //                    intent.putExtra("face_feature", getIntent().getFloatArrayExtra("face_feature"));
            //                    intent.setClass(context, CardInfoActivity.class);
            //                    startActivity(intent);
            //                    timer.cancel();
            //                }
            //            }else{
            //                Log.e(TAG, "get face feature failed " );
            //            }
            //            bitmap.recycle();
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case BROADCAST_READ_CARD_SUCCEED:
                    Bundle bundle = new Bundle();
                    bundle.putFloat("guest_heat", getIntent().getExtras().getFloat("guest_heat"));
                    intent.putExtra("face_feature", getIntent().getFloatArrayExtra("face_feature"));
                    intent.putExtras(bundle);
                    intent.setClass(getBaseContext(), CardInfoActivity.class);
                    startActivity(intent);
                    FaceUtils.getInstance().getSpeaker().speak("读卡成功，请输入手机号");
                    timer.cancel();
                    finish();
                    //        bundle.putFloat("guest_heat", valueOf);
                    Log.d("Constraints","guest_heat==="+getIntent().getExtras().getFloat("guest_heat"));
                    break;
                case BROADCAST_READ_CARD_FAILED:
                    FaceUtils.getInstance().getSpeaker().speak("读卡失败，请重试");
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_card);
        Log.i(TAG, "onCreate: ");
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        readCardServiceIntent.setComponent(
                new ComponentName(READER_SERVICE_PACKAGE, READER_SERVICE_NAME));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(readCardServiceIntent);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_READ_CARD_SUCCEED);
        intentFilter.addAction(BROADCAST_READ_CARD_FAILED);
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        };
        timer.schedule(tt, 20000);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        stopService(readCardServiceIntent);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
