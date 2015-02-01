package com.dhruv.transparency;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemClock;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider{
	



@Override
   public void onUpdate(Context context, AppWidgetManager appWidgetManager,
   int[] appWidgetIds) {
	for(int i=0; i<appWidgetIds.length; i++)
      {
    	  int currentWidgetId = appWidgetIds[i];
    	  Intent intent2 = new Intent(context,Start.class);
    	  PendingIntent pending = PendingIntent.getActivity(context, 0,intent2, 0);
    	  RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
    	  views.setOnClickPendingIntent(R.id.widgetLayout, pending);
    	  DBAdapter db = new DBAdapter(context);
    	  db.open();
    	  Cursor c = db.getAllRecords();
    	  if(c.getCount()>0)
      		{
    		  	c.moveToLast();	    
      			views.setTextViewText(R.id.widget_tv1, c.getString(1));
      			views.setTextViewText(R.id.widget_tv2, c.getString(2));
      			views.setTextViewText(R.id.widget_tv3,"Time Used: "+c.getString(3));
      		}
    	  final Intent intent1 = new Intent(context, Widget.class);
    	  final PendingIntent pending1 = PendingIntent.getService(context, 0, intent1, 0);
    	  final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	  long interval = 1000*60;
    	  alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),interval, pending1);
    	  appWidgetManager.updateAppWidget(currentWidgetId,views);

      }
	
   }
}
