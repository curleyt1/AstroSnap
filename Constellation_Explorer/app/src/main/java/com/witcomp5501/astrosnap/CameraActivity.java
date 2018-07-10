package com.witcomp5501.astrosnap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private static final String  TAG = "AstroSnap::Camera";

    private Mat mGray;
    private Mat imgWithBlobs;
    private MatOfKeyPoint matOfKeyPoints;
    private FeatureDetector blobDetector;
    private JavaCameraView mOpenCvCameraView;
    private boolean processNextFrame = false;
    private static double[][] starArray;

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
                    mOpenCvCameraView.setOnTouchListener(CameraActivity.this);
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
        Log.d(TAG, "Creating and setting view");

        setContentView(R.layout.home_screen);
        mOpenCvCameraView = new JavaCameraView(this, -1);
        setContentView(mOpenCvCameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
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
     * Clean-up resources.
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
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (processNextFrame) {
            Imgproc.cvtColor(inputFrame.rgba(), mGray, Imgproc.COLOR_BGR2GRAY);
            blobDetector.detect(mGray, matOfKeyPoints);
            Features2d.drawKeypoints(mGray, matOfKeyPoints, imgWithBlobs);
            KeyPoint[] keyPoints = matOfKeyPoints.toArray();
            starArray = new double[keyPoints.length][3];
            for (int i=0; i < keyPoints.length; i++) {
                starArray[i][0] = keyPoints[i].pt.x;
                starArray[i][1] = keyPoints[i].pt.y;
                starArray[i][2] = keyPoints[i].size;
            }
            // Sort starArray by area of each star
            Arrays.sort(starArray, new java.util.Comparator<double[]>() {
                public int compare(double[] a, double[] b) { return Double.compare(b[2], a[2]); }
            });

            // Log information on key points.
            Log.i(TAG, matOfKeyPoints.toString());
            Log.i(TAG, matOfKeyPoints.dump());
            for (double[] star : starArray) {
                Log.i(TAG, star[0] + ", " + star[1] + ", " + star[2]);
            }
            processNextFrame = false;
        }
        return inputFrame.rgba();
    }

    /**
     * Sets boolean varialbe processNextFrame to True so that image is processed.
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        processNextFrame = true;
        Toast.makeText(this, "Image Processing", Toast.LENGTH_SHORT).show();
        return true;
    }

    public static double[][] getStarArray() { return starArray; }
}
