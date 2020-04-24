package com.openailab.ailab.utils;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ZyElite
 */
public class CameraUtils {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;


    public static final int DESIRED_PREVIEW_FPS = 30;

    private static int mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static Camera mCamera;
    private static int mCameraPreviewFps;
    private static int mOrientation = 90;

    public static void openFrontalCamera() {
        if (mCamera != null) {
            throw new RuntimeException("camera already initialized!");
        }
        mCamera = Camera.open(mCameraID);
        // 没有摄像头时，抛出异常
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }

        Camera.Parameters parameters = mCamera.getParameters();
        mCameraPreviewFps = CameraUtils.chooseFixedPreviewFps(parameters, CameraUtils.DESIRED_PREVIEW_FPS * 1000);

        parameters.setRecordingHint(true);
        mCamera.setParameters(parameters);
        setPreviewSize(mCamera, CameraUtils.DEFAULT_WIDTH, CameraUtils.DEFAULT_HEIGHT);
        mCamera.setDisplayOrientation(mOrientation);


    }


    public static void startPreviewDisplay(SurfaceTexture surface) {
        try {
            mCamera.setPreviewTexture(surface);
            FaceUtils.getInstance().openRGB(mCamera);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();

    }

    /**
     * 释放相机
     */
    public static void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 开始预览
     */
    public static void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    public static void stopPreview(){
        if(mCamera != null){
            mCamera.stopPreview();
        }
    }

    public static void setPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = calculatePerfectSize(parameters.getSupportedPreviewSizes(),
                expectWidth, expectHeight);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
    }


    public static Camera.Size calculatePerfectSize(List<Camera.Size> sizes, int expectWidth,
                                                   int expectHeight) {
        sortList(sizes);
        Camera.Size result = sizes.get(0);
        boolean widthOrHeight = false;
        for (Camera.Size size : sizes) {
            if (size.width == expectWidth && size.height == expectHeight) {
                result = size;
                break;
            }
            if (size.width == expectWidth) {
                widthOrHeight = true;
                if (Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            } else if (size.height == expectHeight) {
                widthOrHeight = true;
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)) {
                    result = size;
                }
            } else if (!widthOrHeight) {
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)
                        && Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
        }
        return result;
    }

    private static void sortList(List<Camera.Size> list) {
        Collections.sort(list, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size pre, Camera.Size after) {
                if (pre.width > after.width) {
                    return 1;
                } else if (pre.width < after.width) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public static int chooseFixedPreviewFps(Camera.Parameters parameters, int expectedThoudandFps) {
        List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
        for (int[] entry : supportedFps) {
            if (entry[0] == entry[1] && entry[0] == expectedThoudandFps) {
                parameters.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }
        int[] temp = new int[2];
        int guess;
        parameters.getPreviewFpsRange(temp);
        if (temp[0] == temp[1]) {
            guess = temp[0];
        } else {
            guess = temp[1] / 2;
        }
        return guess;
    }

}
