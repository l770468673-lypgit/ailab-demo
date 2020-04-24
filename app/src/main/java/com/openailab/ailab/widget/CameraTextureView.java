package com.openailab.ailab.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.openailab.ailab.utils.CameraUtils;


/**
 * @author ZyElite
 */
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = "rustApp";

    public CameraTextureView(Context context) {
        this(context, null);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "TextureView onSurfaceTextureAvailable");

        Matrix matrix = getTransform(new Matrix());
        matrix.setScale(-1, 1);
        matrix.postTranslate(width, 0);
        setTransform(matrix);

        CameraUtils.openFrontalCamera();
        CameraUtils.startPreviewDisplay(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "TextureView onSurfaceTextureSizeChanged"); // Ignored, Camera does all the work for us

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "TextureView onSurfaceTextureDestroyed");
        CameraUtils.releaseCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

}
