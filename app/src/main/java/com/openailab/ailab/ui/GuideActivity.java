package com.openailab.ailab.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.openailab.ailab.R;
import com.openailab.ailab.ui.FullscreenActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author ZyElite
 */
public class GuideActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private int REQ_GET_CAMERA_PER = 100;

 /*   {
        try {
            Os.setenv("KERNEL_MODE", "2", true);
            Log.e("asd", "开启量化");
        } catch (Exception e) {
            Log.e("asd", "开启失败");
            e.printStackTrace();
        }

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startActivity(new Intent(this, FullscreenActivity.class));
        finish();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请赋予程序相机与读写存储权限！", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        try {
//            Os.setenv("KERNEL_MODE", "2", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean permissions = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        if (!permissions) {
            EasyPermissions.requestPermissions(this, "请赋予程序相机与读写存储权限！", REQ_GET_CAMERA_PER, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        } else {
            startActivity(new Intent(this, FullscreenActivity.class));
            finish();
        }
    }
}
