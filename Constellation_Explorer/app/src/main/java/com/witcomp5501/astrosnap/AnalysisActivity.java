package com.witcomp5501.astrosnap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AnalysisActivity extends Activity {

    private static final String  TAG = "AstroSnap::Analysis";
    public String[][] match = new String[3][31];

    /**
     * On create, call findConstellationMatch and log the match.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        match = findConstellationMatch(MainActivity.templateData);
        Log.i(TAG, "MATCH DETECTED " + match[0][0]);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Return templates rotated based on two stars. First calculates angle of rotation
     * Then calls rotateTemplates to transform the data.
     * @param templateData template constellation data.
     * @param brightest_index the brighter of the two stars.
     * @param second_brightest other star used for rotation.
     * @return
     */
    private double[][][] rotate(String[][][] templateData, int brightest_index, int second_brightest) {
        double[][] userStarData = CameraActivity.getStarArray();
        String[][][] rotatedTemplates = new String[5][89][31];
        double x0, y0, x1, y1, dx, dy;
        double angle = 0;

        x0 = userStarData[brightest_index][0];
        y0 = userStarData[brightest_index][1];
        x1 = userStarData[second_brightest][0];
        y1 = userStarData[second_brightest][1];
        dx = x1 - x0;
        dy = y1 - y0;
//        angle in Radians
        angle = ((180 * (1 - Math.signum(dx)) / 2 + Math.atan(dy / dx) / Math.PI * 180)) / (180 * Math.PI);
//        angle in Degrees
//        angle = 90 * (1-Math.signum(dx)) + Math.atan(dy/dx);
        Log.i(TAG, "Rotation angle: " + angle);

        return rotateTemplates(templateData, angle);
    }
    /**
     * This function is called once that rotation angle has been calculated, rotates template data.
     * @param templateData template constellation data
     * @param angle angle of rotation determined by the rotatefunction
     * @return
     */
    private double[][][] rotateTemplates(String[][][]templateData, double angle) {
        double[][][] rotatedTemplates = new double[5][89][31];
        double x, y, xprime, yprime;

        // For each constellation:
        for (int i = 0; i < 86; i++) {
            int numStars = Integer.parseInt(templateData[0][i][1]);
            // For each x and y, parse values, then calculate rotated values. Formulae:
            // x' = (x * cos(theta)) - (y * sin(theta))
            // y' = (x * sin(theta)) + (y * cos(theta))
            if (i == 9)
                Log.i(TAG, "Constellation: " + templateData[0][i][0] + " Rotated X and Y Values:");
            for(int j = 0; j < numStars; j++) {
                x = Double.parseDouble(templateData[1][i][j]);
                y = Double.parseDouble(templateData[2][i][j]);

//                if angle is in degrees convert to radians
//                xprime = (x * Math.cos((Math.toRadians(angle)))) - (y * Math.sin((Math.toRadians(angle))));
//                yprime = (x * Math.sin(Math.toRadians(angle))) + (y * Math.cos((Math.toRadians(angle))));

//                if angle is in radians
                xprime = ((x * Math.cos(angle)) - (y * Math.sin(angle)));
                yprime = ((x * Math.sin(angle)) + (y * Math.cos(angle)));

//              log data for the Caelum constellation
                if (i == 9) {
                    Log.i(TAG, "x: " + x);
                    Log.i(TAG, "y: " + y);
                    Log.i(TAG, "xprime: " + xprime);
                    Log.i(TAG, "yprime: " + yprime);
                }

                rotatedTemplates[1][i][j] = xprime;
                rotatedTemplates[2][i][j] = yprime;
            }
        }
        return rotatedTemplates;
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
        for(int i=0;i<86;i++)//iterate through each 89 constellations
        {
            //iterate over the user image dataset for the index of the first star in the triplet
            for(int starOne=0;starOne<userStarData.length-2;starOne++)
            {
                double starOne_x = userStarData[starOne][0];
                //iterate over the user image dataset for the index of the second star in the triplet
                for(int starTwo=starOne+1;starTwo<userStarData.length-1;starTwo++)
                {
                    double[][][] rotatedTemplates = rotate(templateData, starOne, starTwo);
                    double starTwo_x = userStarData[starTwo][0];
                    //determine scale of the star pair from user image to be applied to templates
                    double scale_x =  starTwo_x-starOne_x;
                    double scale_y = userStarData[starTwo][1] - userStarData[starOne][1];
                    //temp array to store scale transformed template data
                    double[][] tempTemplate = new double[2][Integer.parseInt(templateData[0][i][1])];

                    //applying the scale transform to the template being looked at
                    for(int j=1;j<Integer.parseInt(templateData[0][i][1]);j++)
                    {
                        tempTemplate[0][j] = rotatedTemplates[1][i][j]*scale_x;
                        tempTemplate[1][j] = rotatedTemplates[2][i][j]*scale_y;
                    }
                    //iterate over the user image dataset for the index of the third star in the user image
                    for(int starThree=starTwo+1;starThree<userStarData.length;starThree++)
                    {
                        double templateXDelta = tempTemplate[0][2] - tempTemplate[0][1];
                        double templateYDelta = tempTemplate[1][2] - tempTemplate[1][1];
                        double xDelta = userStarData[starThree][0] - userStarData[starTwo][0];
                        double yDelta = userStarData[starThree][1] - userStarData[starTwo][1];
//                      log data for the Caelum constellation
                        if (i == 9) {
                            Log.i(TAG, "Inner Comparisons:");
                            Log.i(TAG, "Second star Index: " + starTwo);
                            Log.i(TAG, "Third star Index: " + starThree);
                            Log.i(TAG, "user second star: " + userStarData[starTwo][0] + ", " + userStarData[starTwo][1]);
                            Log.i(TAG, "user third star: " + userStarData[starThree][0] + ", " + userStarData[starThree][1]);
                            Log.i(TAG, "rotated template second star: " + tempTemplate[0][1] + ", " + tempTemplate[1][1]);
                            Log.i(TAG, "rotated template third star: " + tempTemplate[0][2] + ", " + tempTemplate[1][2]);
                            Log.i(TAG, "tempXDelta: " + templateXDelta + ". TempYDelta: " + templateYDelta);
                            Log.i(TAG, "userXDelta: " + xDelta + ". userYDelta" + yDelta);
                            Log.i(TAG, "ARRAY:");
                            Log.i(TAG, "X: " + tempTemplate[0][0] + ", Y: " + tempTemplate[1][0]);
                            Log.i(TAG, "X: " + tempTemplate[0][1] + ", Y: " + tempTemplate[1][1]);
                            Log.i(TAG, "X: " + tempTemplate[0][2] + ", Y: " + tempTemplate[1][2]);
                            Log.i(TAG, "X: " + tempTemplate[0][3] + ", Y: " + tempTemplate[1][3]);
                        }
                        //check to see if a match was found with the triplet set
                        if(xDelta>(templateXDelta*0.9) && xDelta<(templateXDelta*1.1) && yDelta>(templateYDelta*0.9) && yDelta<(templateYDelta*1.1))
                        {
                            //if match was found, save their coordinates into the match[][] array as well as the name of the identified constellation
                            match[0][0] = templateData[0][i][0];
                            match[1][0] = Double.toString(userStarData[starOne][0]);
                            match[2][0] = Double.toString(userStarData[starOne][1]);
                            match[1][1] = Double.toString(userStarData[starTwo][0]);
                            match[2][1] = Double.toString(userStarData[starTwo][1]);
                            match[1][2] = Double.toString(userStarData[starThree][0]);
                            match[2][2] = Double.toString(userStarData[starThree][1]);
                            int starCount = 3;
                            int lastStar=starThree;
                            int nextStar;

                            while(starCount!=Integer.parseInt(templateData[0][i][1])) {
                                for(nextStar = lastStar+1; nextStar<userStarData.length;nextStar++)
                                {

                                    templateXDelta = tempTemplate[0][starCount+1] - tempTemplate[0][starCount];
                                    templateYDelta = tempTemplate[1][starCount+1] - tempTemplate[1][starCount];
                                    xDelta = userStarData[nextStar][0] - userStarData[lastStar][0];
                                    yDelta = userStarData[nextStar][1] - userStarData[lastStar][1];
                                    if (xDelta > (templateXDelta * 0.9) && xDelta < (templateXDelta * 1.1) && yDelta > (templateYDelta * 0.9) && yDelta < (templateYDelta * 1.1)) {
                                        match[1][starCount + 1] = Double.toString(userStarData[nextStar][0]);
                                        match[2][starCount + 1] = Double.toString(userStarData[nextStar][1]);
                                        starCount++;
                                        break;
                                    }
                                }
                                lastStar = nextStar;
                            }
                            return match;
                        }
                    }
                }
            }
        }
        return new String[3][31];
    }
}
