# Constellation Explorer
Constellation explorer is an Android application for constellation detection that utilizes the OpenCV library.

**Note:** This ReadMe is a markdown file that is best viewed on the projects github page, located at the following link:

https://github.com/curleyt1/AstroSnap

## Constellation Explorer APK

The binary for this project can be downloaded from our releases on github via the following link:

https://github.com/curleyt1/AstroSnap/releases/tag/v0.1-alpha

The APK, which is an installer for Android devices, is called `app-debug.apk`. This readme contains instructions on building this APK from the project source files.

## Project Files
#### Constellation_Explorer
Folder containing all android studio project files for application.
##### App subfolder
This is the subfolder containing our Java Source files. The source code for the application's three activities are found in the path:

`Project Files/Constellation_Explorer/app/src/main/java/com/witcomp5501/astrosnap`

Here you find the files:
`MainActivity.java`
`CameraActivity.java` and
`AnalysisActivity.java`

#### UX_Concept
Folder containing sketches for UX initial design concepts

#### templates
Folder containing template images used from astronomyonline.org

#### test_images
Folder containing images generated or collected to be used for testing the application

#### constellations_database.csv
CSV file containing all data extracted from constellation template images

#### constellations_database.xlsx
Excel sheet used to generate CSV file

#### constellations_wiki_links.txt
Text file containing the wikipedia link for each constellation

## How to build from source with Android Studio
1. Download and install the Android Studio IDE from https://developer.android.com/studio/
2. Launch Android Studio.
3. Select File >> New >> Import Project
4. Navigate to the directory of this project, select the `Constellation_Explorer` folder in `Project Files`, and click `ok`.
5. The IDE may download and configure gradle for the project. This takes a couple of minutes.
6. Once that is finished build the project by clicking Build >> Make Project -- or by clicking the hammer in the top right menu.
7. This will build the application's .apk file, called `app_debug.apk` which is located in the directory `Constellation_Explorer\app\build\outputs\debug\`.
8. To run the application on an android emulator or a connected android device with USB Debugging enabled: Click Run >> Run 'app' in Android Studio, or click the green arrow.
9. If you do not yet have a virtual android device, you can create one here by clicking `Create New Virtual Device`, and following through the steps. An example device that will work for this project is a 'Nexus 5X' phone running android Oreo, API level 27.
10. You can also simply copy this .apk to an android device and install it if unknown sources are enabled in the device settings.

## Installing OpenCV Manager on a virtual Android device
Constellation Explorer utilizses the OpenCV Android library. Loading this library **requires** that the device installs another application called OpenCV Manager.

The following step does not need to be done if running the application on a real android device. It is only for virtual devices in the emulator that cannot use the google play store:

When launching the app, the user is prompted to install OpenCV Manager on their device. The virtual device you have just created does not have the google play store, so if you wish to test the application on an emulator, the OpenCV manager app must be installed manuallly using `adb`.

The OpenCV manager apk is bundled in with the project zip file. to install run the following command in the Android Studio command line while the virtual device is running:

`adb install <INSERT_PATH_TO_DIRECTORY_HERE>\OpenCV_3.4.1_Manager_3.41_x86.apk`

## Using a webcam with a virtual Android device in Android Studio
1. From android studio launch the AVD Manager under Tools >> AVD Manager.
2. Click the pencil icon next to your virtual device to edit the device.
3. Click show advanced settings, and change the **Back Camera** to use your webcam.
