package com.dhruv.transparency;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.r0adkll.slidr.Slidr;

import java.io.File;

public class Picture extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);
        Slidr.attach(this);
        Bundle extras = getIntent().getExtras();
        File imgFile = new  File(extras.getString("path"));
        Log.d("path",extras.getString("path"));
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.Picture);
            myImage.setImageBitmap(myBitmap);
        }
    }
}