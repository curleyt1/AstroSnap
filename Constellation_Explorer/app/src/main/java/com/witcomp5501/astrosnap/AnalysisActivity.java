package com.witcomp5501.astrosnap;

import android.app.Activity;


public class AnalysisActivity extends Activity {

    private static final String  TAG = "AstroSnap::AnalysisActivity";

    /**
     *
     */
    @Override

    public String[][] match = new String[3][31];

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

    private String[][] findConstellationMatch(String[][][] templateData) {
        double[][] userStarData = CameraActivity.getStarArray();
        String[][] match = new String[3][31];
        //look at constellation template
        //search through user image points for a sizing and distance match between two points to the two brightest stars in the constellation
        //if match is found, search for rest of the stars in constellation
        for(int i=0;i<89;i++)//loop through constellations
        {
            for(int starOne=0;starOne<userStarData.length-2;starOne++)
            {
                double starOne_x = userStarData[0][starOne];
                for(int starTwo=starOne+1;starTwo<userStarData.length-1;starTwo++)
                {
                    double starTwo_x = userStarData[0][starTwo];
                    //take x and y differences between two stars from user image
                    double scale =  starTwo_x-starOne_x;
                    //calculate percentage
                    double[][] tempTemplate = new double[2][Integer.parseInt(templateData[0][i][0])];
                    for(int j=0;j<Integer.parseInt(templateData[0][i][1]);j++)
                    {
                        tempTemplate[0][j] = Double.parseDouble(templateData[1][i][j])*scale;
                        tempTemplate[1][j] = Double.parseDouble(templateData[2][i][j]);
                    }
                    for(int starThree=starTwo+1;starThree<userStarData.length;starThree++)
                    {
                        double templateXDelta = tempTemplate[1][2] - tempTemplate[1][1];
                        double templateYDelta = tempTemplate[2][2] - tempTemplate[2][1];
                        double xDelta = userStarData[0][starThree] - userStarData[0][starTwo];
                        double yDelta = userStarData[1][starThree] - userStarData[1][starTwo];
                        if(xDelta>(templateXDelta*0.9) && xDelta<(templateXDelta*1.1) && yDelta>(templateYDelta*0.9) && yDelta<(templateYDelta*1.1))
                        {
                            match[0][0] = templateData[0][i][0];
                            match[1][0] = Double.toString(userStarData[0][starOne]);
                            match[2][0] = Double.toString(userStarData[1][starOne]);
                            match[1][1] = Double.toString(userStarData[0][starTwo]);
                            match[2][1] = Double.toString(userStarData[1][starTwo]);
                            match[1][2] = Double.toString(userStarData[0][starThree]);
                            match[2][2] = Double.toString(userStarData[1][starThree]);
                            //find rest of the stars
                            for(int k=starThree;k<userStarData.length-1;k++)
                            {
                                for(int m=starThree+1;m<userStarData.length;m++)
                                {
                                    templateXDelta = tempTemplate[1][m] - tempTemplate[1][k];
                                    templateYDelta = tempTemplate[2][m] - tempTemplate[2][k];
                                    xDelta = userStarData[0][m] - userStarData[0][k];
                                    yDelta = userStarData[1][m] - userStarData[1][k];
                                    if(xDelta>(templateXDelta*0.9) && xDelta<(templateXDelta*1.1) && yDelta>(templateYDelta*0.9) && yDelta<(templateYDelta*1.1))
                                    {
                                        match[1][k+1] = Double.toString(userStarData[0][m]);
                                        match[2][k+1] = Double.toString(userStarData[1][m]);
                                    }
                                }
                            }

                            return match;
                        }
                    }
                }
            }
        }
        return NULL;
    }
}
