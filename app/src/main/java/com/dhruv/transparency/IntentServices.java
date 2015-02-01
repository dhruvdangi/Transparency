package com.dhruv.transparency;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class IntentServices  extends IntentService{
	

	public IntentServices() {
		super("name");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
    	DBAdapter db = new DBAdapter(this);
        String UnlockTime = intent.getStringExtra("UnlockTime");
        String UnlockDate = intent.getStringExtra("UnlockDate");
        String PackageName = intent.getStringExtra("PackageName");
        String TimeUsed = intent.getStringExtra("TimeUsed");
        long TimeUsedInMs = intent.getLongExtra("TimeUsedInMs",0);
        boolean resetTimeUsed =intent.getBooleanExtra("resetTimeUsed",false);
		db.open();        
		SharedPreferences sharedPref = this.getSharedPreferences("variables", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.dhruv.transparency_preferences", Context.MODE_PRIVATE);
        int first=sharedPref.getInt("First", 0);
		int last=sharedPref.getInt("Last", 0);
		int maxEntries=Integer.parseInt(sharedPreferences.getString("noOfEntries", "-1"));
        long TotalTimeUsed=sharedPref.getLong("TotalTimeUsed", 0)+TimeUsedInMs;
        if(!PackageName.equals("")){
		if(last<=maxEntries || maxEntries==-1)
		{
			if(db.insertRecord(UnlockTime,UnlockDate, TimeUsed, PackageName)!=-1)
			{
				if(first==0)
				first++;
				last++;
			}
		}
		else
		{
            db.deleteData((long) first);
			first++;
			if(db.insertRecord(UnlockTime,UnlockDate, TimeUsed, PackageName)!=-1)
				last++;
		}
        }
		SharedPreferences.Editor editor = sharedPref.edit();
	    editor.putInt("First", first);
	    editor.putInt("Last", last);
        if(resetTimeUsed)
        {editor.putLong("TotalTimeUsed", 0);
            Log.d("reset","yes"+resetTimeUsed);}
        else
            editor.putLong("TotalTimeUsed", TotalTimeUsed);

	    editor.commit();
	    ScreenService.intentServiceComplete=true;
	}

}

