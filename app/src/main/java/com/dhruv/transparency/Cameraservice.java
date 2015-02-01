package com.dhruv.transparency;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static android.hardware.Camera.PictureCallback;

public class Cameraservice extends Service {
    Camera cam;
    Camera.Parameters param;
    String startTime,startDate;
    int MAX_PICTURES;
    static boolean CameraserviceRunning=false;
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("CAMERA", "onPictureTaken - raw");
            camera.stopPreview();
            camera.release();
        }
    };

    private PictureCallback jpgCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("CAMERA", "onPictureTaken - jpg");
            try {

                FileOutputStream outStream = new FileOutputStream(getExternalFilesDir(null).getAbsolutePath()+"/"+startDate.replace(":","_")+" "+startTime.replace(":","_")+".jpg");
                Log.d("output",getExternalFilesDir(null).getAbsolutePath()+"/"+startDate+" "+startTime+".jpg");
                outStream.write(data);
                outStream.close();
                Log.d("TAG", "onPictureTaken - wrote bytes: " + data.length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            Log.d("TAG", "onPictureTaken - jpeg");
            camera.stopPreview();
            camera.release();
            CameraserviceRunning = false;
            stopSelf();
            File f = new File(getExternalFilesDir(null),"/");
            File [] files = f.listFiles();
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {

                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return +1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }

            });
            ArrayList<String> sortedList = new ArrayList<String>();
            for(int i=0;i<files.length;i++){
                sortedList.add(files[i].toString());
            }
            Log.d("sortedList",""+sortedList.toString());
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("com.dhruv.transparency_preferences",getApplicationContext().MODE_PRIVATE);
            MAX_PICTURES = Integer.parseInt(preferences.getString("MAX_PICTURES", "50"));
            Log.d("max",""+MAX_PICTURES);
            if(MAX_PICTURES!=-1){
                if(sortedList.size()>=MAX_PICTURES){
                    for(int i=0;i<sortedList.size()-MAX_PICTURES;i++){
                        File file = new File(sortedList.get(i));
                        if(file.exists())
                        {
                            file.delete();
                            Log.d("File Deleted",""+file.toString());
                        }
                    }
                }
            }
        }
    };


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CameraserviceRunning = true;
        Bundle extras = intent.getExtras();
        startDate = extras.getString("startDate");
        startTime = extras.getString("startTime");
        Boolean camOpen=false;
        try {  Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int camIdx = 0; camIdx <= Camera.getNumberOfCameras()-1; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cam = Camera.open(camIdx);
                    camOpen=true;
                    Log.d("Camera","Found front camera at "+camIdx);
                    break;
                }
                else
                    Log.d("Camera","not front cam at "+camIdx);
            }
            if (camOpen==false){
                cam = Camera.open();
            }
            Log.i("CAMERA", "Success");
        } catch (RuntimeException e) {
            Log.e("CAMERA", "Camera currently unavailable");
            e.printStackTrace();
        }
        try {
            param = cam.getParameters();
            if (param.getSupportedWhiteBalance().contains(
                    Camera.Parameters.WHITE_BALANCE_AUTO)) {
                param.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                Log.d("Camera Parameters", "white balance auto");
            }
            if (param.getSupportedSceneModes().contains(
                    Camera.Parameters.SCENE_MODE_AUTO)) {
                param.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                Log.d("Camera Parameters", "scene mode auto");
            }
            cam.setParameters(param);
            cam.setDisplayOrientation(90);
            Log.i("CAMERA", "Success");
        } catch (Exception e1) {
            Log.e("CAMERA", "Parameter problem");
            e1.printStackTrace();
        }
        try {
            SurfaceTexture st = new SurfaceTexture(10);
            cam.setPreviewTexture(st);
            cam.startPreview();
            Log.i("CAMERA", "Success");
        } catch (Exception e) {
            Log.e("CAMERA", "Surface Problem");
            e.printStackTrace();
        }
        try {
            cam.takePicture(null, null,jpgCallback );
            Log.i("CAMERA", "Success ;)");
        } catch (Exception e) {
            Log.e("CAMERA", "Click Failure");
            e.printStackTrace();
        }
        // Commented out following line and moved it into your callbacks
        //cam.release();
        return super.onStartCommand(intent, flags, startId);
    }

}
