package com.witcomp5501.astrosnap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import java.io.*;
import java.lang.String;
import java.util.Scanner;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener, View.OnTouchListener {

    private static final String  TAG = "AstroSnap::MainActivity";

    private Mat mGray;
    private Mat imgWithBlobs;
    private MatOfKeyPoint matOfKeyPoints;
    private FeatureDetector blobDetector;

    private JavaCameraView mOpenCvCameraView;
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
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
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

        String[][] wiki = new String [89][2]; //wiki[n][0] = constellation_name  wiki[n][1] = wiki_link
        String[][][] templateData = new String [5][89][31]; //
        wiki = readWikiLinks();

        setContentView(R.layout.home_screen);

        mOpenCvCameraView = new JavaCameraView(this, -1);
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
    public Mat onCameraFrame(Mat inputFrame) {
//        Imgproc.cvtColor(inputFrame, mGray, Imgproc.COLOR_BGR2GRAY);
//        blobDetector.detect(mGray, matOfKeyPoints);
//        Features2d.drawKeypoints(mGray, matOfKeyPoints, imgWithBlobs);
//        return imgWithBlobs;
        return inputFrame;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/sample_picture_" + currentDateandTime + ".jpg";
        //mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void permitCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    //this function reads into a 3D array the data from the constellation templates
    //this will allow for easy searching through the data when looking for a match
    private String[][][] readTemplateData() {
        try {
            String[][][] data = new String [5][89][31];
            int numStars; //maybe use this, would have to insert star counts in csv
            DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format("constellations_database.csv")));
            Scanner sc = new Scanner(textFileStream);
            sc.useDelimiter(",");
            while(sc.hasNext()){
                for(int constell=0;constell<89;constell++){
                    data[0][constell][0] = sc.next();
                    numStars = sc.nextInt();
                    for(int dataCat=0;dataCat<5;dataCat++){
                        for(int star=0;star<numStars;star++){

                        }
                    }
                }
                //scanner delimiter use of commas
                //star will have to be reset after every row
                //constell should go from 0-88
                //data cat needs to be reset after each constellation
            }
            sc.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //this function reads into a 2D array the wikipedia links for each of the 88 constellations
    private String[][] readWikiLinks() {
        try {
            String[][] data = new String [89][2];
            DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format("constellations_wiki_links.txt")));
            Scanner sc = new Scanner(textFileStream);
            for(int x=0;x<89;x++){
                data[x][0] = sc.next();
                data[x][1] = sc.next();
            }
            sc.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
