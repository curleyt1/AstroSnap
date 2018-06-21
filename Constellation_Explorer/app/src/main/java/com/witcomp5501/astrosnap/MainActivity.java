package com.witcomp5501.astrosnap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener {

    private static final String  TAG = "AstroSnap::MainActivity";

    private Mat mGray;
    private Mat imgWithBlobs;
    private MatOfKeyPoint matOfKeyPoints;
    private FeatureDetector blobDetector;

    private CameraBridgeViewBase mOpenCvCameraView;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

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
    public void onResume() {
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
        permitCamera();
        Log.d(TAG, "Creating and setting view");

        setContentView(R.layout.home_screen);

        mOpenCvCameraView = (CameraBridgeViewBase) new JavaCameraView(this, -1);
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(mOpenCvCameraView);
                mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
            }
        });
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    /**
     * Stub required for CameraViewListener
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat(height, width, CvType.CV_8UC1);
        imgWithBlobs = new Mat();

        matOfKeyPoints = new MatOfKeyPoint();
        blobDetector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
    }

    /**
     * Clean-up
     */
    public void onCameraViewStopped() {
        mGray.release();
        imgWithBlobs.release();
        matOfKeyPoints.release();
        blobDetector.empty();
    }

    /**
     * Function called on every camera frame.
     * @param inputFrame
     * @return
     */
    public Mat onCameraFrame(Mat inputFrame) {
        Imgproc.cvtColor(inputFrame, mGray, Imgproc.COLOR_BGR2GRAY);
        blobDetector.detect(mGray, matOfKeyPoints);
        Features2d.drawKeypoints(mGray, matOfKeyPoints, imgWithBlobs);
        return imgWithBlobs;
    }

    public void permitCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }
}
