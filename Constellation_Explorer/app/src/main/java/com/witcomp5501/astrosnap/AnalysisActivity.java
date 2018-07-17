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

    //this function searches through user image and template datasets to find a match between the data
    //this function looks for matches by analyzing each triplet set of stars in the user image data set
    //first look at the first two stars in the triplet to determine the scale of their coordinates and apply that scale to the template being looked at
    //then iterate over the user dataset for each possible triplet set, checking to see if there is a distance match
    //within a 10% window of the distance between the corresponding stars from the template
    //keep iterating through until a triplet match has been found
    //then iterate over the user data starting from the third star in the triplet match to find the rest of the stars in the constellation
    private String[][] findConstellationMatch(String[][][] templateData) {
        double[][] userStarData = CameraActivity.getStarArray();//grab dataset from user image from the camera activity
        String[][] match = new String[3][31];//this array will contain the coordinates of all the stars in identified constellation
        for(int i=0;i<89;i++)//iterate through each 89 constellations
        {
            //iterate over the user image dataset for the index of the first star in the triplet
            for(int starOne=0;starOne<userStarData.length-2;starOne++)
            {
                double starOne_x = userStarData[0][starOne];
                //iterate over the user image dataset for the index of the second star in the triplet
                for(int starTwo=starOne+1;starTwo<userStarData.length-1;starTwo++)
                {
                    double starTwo_x = userStarData[0][starTwo];
                    //determine scale of the star pair from user image to be applied to templates
                    double scale =  starTwo_x-starOne_x;
                    //temp array to store scale transformed template data
                    double[][] tempTemplate = new double[2][Integer.parseInt(templateData[0][i][0])];
                    //applying the scale transform to the template being looked at
                    for(int j=0;j<Integer.parseInt(templateData[0][i][1]);j++)
                    {
                        tempTemplate[0][j] = Double.parseDouble(templateData[1][i][j])*scale;
                        tempTemplate[1][j] = Double.parseDouble(templateData[2][i][j]);
                    }
                    //iterate over the user image dataset for the index of the third star in the user image
                    for(int starThree=starTwo+1;starThree<userStarData.length;starThree++)
                    {
                        double templateXDelta = tempTemplate[1][2] - tempTemplate[1][1];
                        double templateYDelta = tempTemplate[2][2] - tempTemplate[2][1];
                        double xDelta = userStarData[0][starThree] - userStarData[0][starTwo];
                        double yDelta = userStarData[1][starThree] - userStarData[1][starTwo];
                        //check to see if a match was found with the triplet set
                        if(xDelta>(templateXDelta*0.9) && xDelta<(templateXDelta*1.1) && yDelta>(templateYDelta*0.9) && yDelta<(templateYDelta*1.1))
                        {
                            //if match was found, save their coordinates into the match[][] array as well as the name of the identified constellation
                            match[0][0] = templateData[0][i][0];
                            match[1][0] = Double.toString(userStarData[0][starOne]);
                            match[2][0] = Double.toString(userStarData[1][starOne]);
                            match[1][1] = Double.toString(userStarData[0][starTwo]);
                            match[2][1] = Double.toString(userStarData[1][starTwo]);
                            match[1][2] = Double.toString(userStarData[0][starThree]);
                            match[2][2] = Double.toString(userStarData[1][starThree]);
                            //iterate over the rest of the user image dataset to find rest of the stars in the matching constellation
                            for(int k=starThree;k<userStarData.length-1;k++)
                            {
                                for(int m=starThree+1;m<userStarData.length;m++)
                                {
                                    templateXDelta = tempTemplate[1][m] - tempTemplate[1][k];
                                    templateYDelta = tempTemplate[2][m] - tempTemplate[2][k];
                                    xDelta = userStarData[0][m] - userStarData[0][k];
                                    yDelta = userStarData[1][m] - userStarData[1][k];
                                    //check to see if the specific star is the next star in the constellation
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
