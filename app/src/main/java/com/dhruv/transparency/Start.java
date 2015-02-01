package com.dhruv.transparency;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class Start extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        Typeface proxima = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/proxima1.ttf");
	    final SharedPreferences sharedPref = Start.this.getSharedPreferences("com.dhruv.transparency_preferences", Context.MODE_PRIVATE);
	    if(sharedPref.getBoolean("enablePassword", false))
	  	{
	    	setContentView(R.layout.activity_start_password);
	    }
	    else

	    {
	    	setContentView(R.layout.activity_start);
            final ShimmerTextView shimmer_tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
            shimmer_tv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    final int height = bottom - top;
                    float size = shimmer_tv.getTextSize();
                    Log.d("height",""+ height+"::"+shimmer_tv.getLineHeight()+":::"+size);
                    if (height>shimmer_tv.getLineHeight()){
                        size = size - 5;
                        shimmer_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
                    }
                }
            });
            shimmer_tv.setTypeface(proxima);
            final Shimmer shimmer = new Shimmer();
            shimmer.setRepeatCount(1).start(shimmer_tv);
	    	Timer t= new Timer();
	    	final Intent i=new Intent(this,MainActivity.class);
	    	t.schedule(new TimerTask() {
			   public void run() {
                   //shimmer.cancel();
                   startActivity(i);
                   finish();
			   }
			}, 1700);
	    }
	    
	}
	public void submit(View v) {
        SharedPreferences sharedPref = Start.this.getSharedPreferences("com.dhruv.transparency_preferences", Context.MODE_PRIVATE);
        EditText et = (EditText) findViewById(R.id.et);
        if (et.getText().toString().equals(sharedPref.getString("password", "1234")) && !et.getText().toString().equals("")) {
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

        } else {

            et.setText("");
            Toast.makeText(getBaseContext(), "Incorrect Password (Default : 1234)", Toast.LENGTH_SHORT).show();

        }
    }
}
