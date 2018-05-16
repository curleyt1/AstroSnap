package com.witcomp5501.astrosnap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener {

    private static final String  TAG = "AstroSnap::MainActivity";
    private CameraBridgeViewBase mOpenCvCameraView;

    private int ScreenWidth;
    private int ScreenHeight;

    /**
     * Works with OpenCV Manager in asynchronous fashion.
     * OnManagerConnected callback will be called in UI thread, when initialization finishes.
     * It is not allowed to use OpenCV calls or load dependent libs before invoking this callback.
     */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    /**
     * Re-load OpenCV when app is resumed
     */
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    /**
     * Initializes OpenCV Camera View.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d(TAG, "Creating and setting view");
        mOpenCvCameraView = (CameraBridgeViewBase) new JavaCameraView(this, -1);
        setContentView(mOpenCvCameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        //setContentView(R.layout.activity_main);
    }

    /**
     * Stub required for CameraViewListener
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    public void onCameraViewStarted(int width, int height) {
        ScreenWidth = width;
        ScreenHeight = height;
    }

    /**
     * Stub required for CameraViewListener
     */
    public void onCameraViewStopped() {
    }

    /**
     * Stub required for CameraViewListener
     * @param inputFrame
     * @return
     */
    public Mat onCameraFrame(Mat inputFrame) {
        return inputFrame;
    }

}
