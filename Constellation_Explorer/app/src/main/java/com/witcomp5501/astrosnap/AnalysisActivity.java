package com.witcomp5501.astrosnap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        setContentView(R.layout.result_screen);
        TextView textView   = (TextView) findViewById(R.id.textView2);

        if (match[0][0] != null)
            textView.setText(match[0][0]);
        else
            textView.setText("No Match Found");

        // In case there is not match link to wikipedia main page
        String link="https://www.wikipedia.org/";
        for(int i=0;i<89;i++){
            if(MainActivity.wiki[i][0].equals(match[0][0]))
                link = MainActivity.wiki[i][1];
        }

//      Fetch user image and use it as background in text view.
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        Bitmap bmp = BitmapFactory.decodeFile("/sdcard/AstroSnap_image.jpg");
        imageView.setImageBitmap(bmp);

        Button wikiButton = (Button)findViewById(R.id.button4);
        final String finalLink = link;
        wikiButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW, Uri.parse(finalLink));
                    startActivity(browserIntent);
                }
            });
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
        // Angle in Radians
        angle = Math.atan(dx/dy);
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

            for(int j = 0; j < numStars; j++) {
                x = Double.parseDouble(templateData[1][i][j]);
                y = Double.parseDouble(templateData[2][i][j]);

                // For each x and y, parse values, then calculate rotated values. Formulae:
                // x' = (x * cos(theta)) - (y * sin(theta))
                // y' = (x * sin(theta)) + (y * cos(theta))
                xprime = ((x * Math.cos(angle)) - (y * Math.sin(angle)));
                yprime = ((x * Math.sin(angle)) + (y * Math.cos(angle)));

                // Swapping x and y coordinates to flip templates to match the way the camera graphs the blobs
                rotatedTemplates[1][i][j] = yprime;
                rotatedTemplates[2][i][j] = xprime;
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
        int templateNumStars;
        for(int i=0;i<86;i++)//iterate through each 89 constellations
        {
            templateNumStars = Integer.parseInt(templateData[0][i][1]);
            //iterate over the user image dataset for the index of the first star in the triplet
            for(int starOne=0;starOne<userStarData.length-2;starOne++)
            {
                double starOne_x = userStarData[starOne][0];
                //iterate over the user image dataset for the index of the second star in the triplet
                for(int starTwo=starOne+1;starTwo<userStarData.length-1;starTwo++)
                {
                    double[][][] rotatedTemplates = rotate(templateData, starOne, starTwo);
                    double starTwo_x = userStarData[starTwo][0];
                    //iterate over the user image dataset for the index of the third star in the user image
                    for(int starThree=starTwo+1;starThree<userStarData.length;starThree++)
                    {
                        // Scale using distance between brightest two stars, calculate ratios.
                        double templateXDelta = (rotatedTemplates[1][i][2]-rotatedTemplates[1][i][1])/(rotatedTemplates[1][i][1]-rotatedTemplates[1][i][0]);
                        double templateYDelta = (rotatedTemplates[2][i][2]-rotatedTemplates[2][i][1])/(rotatedTemplates[2][i][1]-rotatedTemplates[2][i][0]);
                        double xDelta = (userStarData[starThree][0]-userStarData[starTwo][0])/(userStarData[starTwo][0]-userStarData[starOne][0]);
                        double yDelta = (userStarData[starThree][1]-userStarData[starTwo][1])/(userStarData[starTwo][1]-userStarData[starOne][1]);

                        //check to see if a match was found with the triplet set
                        if(templateNumStars<=userStarData.length && xDelta>(templateXDelta*0.9) && xDelta<(templateXDelta*1.1) && yDelta>(templateYDelta*0.9) && yDelta<(templateYDelta*1.1))
                        {
                            Log.i(TAG, "Matched first Triplet of " + templateData[0][i][0]);
                            //if match was found, save their coordinates into the match[][] array as well as the name of the identified constellation
                            match[0][0] = templateData[0][i][0];
                            match[1][0] = Double.toString(userStarData[starOne][0]);
                            match[2][0] = Double.toString(userStarData[starOne][1]);
                            match[1][1] = Double.toString(userStarData[starTwo][0]);
                            match[2][1] = Double.toString(userStarData[starTwo][1]);
                            match[1][2] = Double.toString(userStarData[starThree][0]);
                            match[2][2] = Double.toString(userStarData[starThree][1]);

                            return match;
                        }
                    }
                }
            }
        }
        // If no match was found return empty array.
        return new String[3][31];
    }
}
