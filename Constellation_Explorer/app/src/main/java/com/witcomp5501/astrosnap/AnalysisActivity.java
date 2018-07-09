package com.witcomp5501.astrosnap;

import android.app.Activity;


public class AnalysisActivity extends Activity {

    private static final String  TAG = "AstroSnap::AnalysisActivity";

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Process the array to represent locations relative to two particular stars,
     * Where star 1 will be at origin (0,0) and star 2 will be at point (0,1)
     * @param starArray
     * @param star1 int index in star array for first (larger) star
     * @param star2 int index in star array for second (smaller) star
     * @return
     */
    private double[][] normalize(double[][] starArray, int star1, int star2) {

    }
}
