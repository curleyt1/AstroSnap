package com.witcomp5501.astrosnap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import java.util.Arrays;
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

public class MainActivity extends Activity {

    private static final String  TAG = "AstroSnap::MainActivity";
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

    //templateData[][][] will store in memory all the data pulled from the constellation template images
    //x values are categories of data on constellation, y values are specific constellation, z values are specific star in constellation
    public static String[][][] templateData = new String [5][89][31];
    public static String[][] wiki = new String [89][2];

    @Override
    public void onResume() {
        super.onResume();
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

        //loading into memory the file data
        wiki = readWikiLinks();
        templateData = readTemplateData();

        //start the user at the home screen
        setContentView(R.layout.home_screen);

        //waiting for user to click the "find constellation" button to open the camera, then switch to camera activity
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    /**
     * This function is called when the application starts to ensure camera permissions are granted.
     */
    private void permitCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * This function launches the CameraActivity.java
     */
    private void startCamera() {
        Intent launchCamera = new Intent(this, CameraActivity.class);
        startActivity(launchCamera);
    }

    //this function reads into a 3D array the data from the constellation templates
    //this will allow for easy searching through the data when looking for a match
    private String[][][] readTemplateData() {
        try {
            Log.i(TAG, "Reading Template Data");
            String[][][] data = new String [5][89][31];
            String numStars;
            DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format("constellations_database.csv")));
            Scanner sc = new Scanner(textFileStream);
            sc.useDelimiter(",|\\n");
            while(sc.hasNext()){
                for(int constell=0;constell<89;constell++){
                    data[0][constell][0] = sc.next();
                    numStars = sc.next();
                    data[0][constell][1] = numStars;
                    for(int dataCat=1;dataCat<5;dataCat++){
                        for(int star=0;star<Integer.parseInt(numStars);star++){
                            data[dataCat][constell][star] = sc.next();
                        }
                    }
                }
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
            Log.i(TAG, "Reading Wiki Links");
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
