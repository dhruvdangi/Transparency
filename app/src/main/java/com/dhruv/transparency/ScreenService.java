package com.dhruv.transparency;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;


public class ScreenService extends Service {
	BroadcastReceiver mReceiver;
    static String startDate;
    static boolean screenOn;
    static String startTime;
    static ArrayList <String> apps=new ArrayList<String>();
    static long TimeUsed=0;
    static long notificationDelay;
    static boolean resetTimeUsed=false;
	static long screenOnTimer=0;
	final static Timer timer = new Timer();
	public static boolean intentServiceComplete=false;

@Override
	public void onCreate() {
     IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
     filter.addAction(Intent.ACTION_SCREEN_OFF);
     mReceiver = new ScreenReceiver();
     registerReceiver(mReceiver, filter);
	 super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
		// TODO Auto-generated method stub
	}
	
	public static class ScreenReceiver extends BroadcastReceiver{
		
		  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
			  
			      for(int i=0; i<appWidgetIds.length; i++)
			      {
			    	  int currentWidgetId = appWidgetIds[i];
			    	  Intent intent2 = new Intent(context,Start.class);
			    	  PendingIntent pending = PendingIntent.getActivity(context, 0,intent2, 0);
			    	  RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_main);
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
			    	  appWidgetManager.updateAppWidget(currentWidgetId,views);}
		    }

		@Override
		public void onReceive(final Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
            SharedPreferences sp = context.getSharedPreferences("variables", 0);
            long TotalTimeUsed = sp.getLong("TotalTimeUsed",0);
            SharedPreferences applist = context.getSharedPreferences("AppList",0);
            SharedPreferences preferences = context.getSharedPreferences("com.dhruv.transparency_preferences",context.MODE_PRIVATE);
            String s = applist.getString("whitelist","  ");
            final List<String> whitelistApps = Arrays.asList(s.substring(1, s.length() - 1).split(", "));;
            s = applist.getString("blacklist","  ");
            final List<String> blacklistApps = Arrays.asList(s.substring(1, s.length() - 1).split(", "));;
			Intent mServiceIntent;
    		timer.cancel();
            new Timer();
			if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			{
				Intent startServiceIntent = new Intent(context, ScreenService.class);
		        context.startService(startServiceIntent);
			}
	        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT))
	        {
                 if(preferences.getBoolean("notification",false)) {
                     notificationDelay = Long.parseLong(preferences.getString("notificationDelay", "10800"));
                     if (TotalTimeUsed > notificationDelay) {
                             sendNotification(context,notificationDelay);
                             resetTimeUsed = true;
                     }
                 }
                screenOn=true;
	        	screenOnTimer=System.currentTimeMillis()/1000;
	            Calendar c = Calendar.getInstance();
	    		SimpleDateFormat date = new SimpleDateFormat("dd MMMM yyyy" );
	    		SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
	    		startDate = date.format(c.getTime());
	    		startTime = time.format(c.getTime());
	    		context.getPackageManager();
                Log.d("picture",""+preferences.getBoolean("enablePicture",false));
                if(preferences.getBoolean("enablePicture",false)){

                    Intent pic = new Intent(context, Cameraservice.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    pic.putExtra("startDate",startDate);
                    pic.putExtra("startTime",startTime);
                    if(!Cameraservice.CameraserviceRunning)
                        context.startService(pic);
                }
                class getOpenedApps extends Thread{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						while(screenOn)
						{
						ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
	    	    		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
	    	    		final String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
	    	    		final PackageManager pm = context.getPackageManager();
	    	    		        	try {
	    	    		    			PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
	    	    		    			String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
	    	    		    			if(!apps.contains(foregroundTaskAppName.toString()))
	    	    		    				{
	    	    		    					if(!whitelistApps.contains(foregroundTaskAppName.toString()))
                                                {
                                                    Log.d("App", foregroundTaskAppName);
                                                    apps.add(foregroundTaskAppName.toString());

                                                }
                                                if(blacklistApps.contains(foregroundTaskAppName.toString()))
                                                {
                                                    sendNotification("You promised not to use "+foregroundTaskAppName.toString());
                                                }
	    	    		    				}
	    	    		    			}
	    	    		        		catch (NameNotFoundException e) {
	    	    		    			// TODO Auto-generated catch block
	    	    		    			e.printStackTrace();
	    	    		    				}
	    	    	   try {
						Thread.sleep(2000);
	    	    	   } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
	    	    	   	}

	    	    	   }
						if(!screenOn)
		    	    	   {
		    	    		   apps.clear();
		    	    	   }
					}

                    public void sendNotification(String message) {
                        Intent notificationIntent = new Intent(context, Start.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(context,
                                1, notificationIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT);

                        NotificationManager nm = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);

                        Notification.Builder builder = new Notification.Builder(context);

                        builder.setContentIntent(contentIntent)
                                .setSmallIcon(R.drawable.notif)
                                .setTicker(message)
                                .setWhen(System.currentTimeMillis())
                                .setAutoCancel(true)
                                .setContentTitle(message)
                                .setContentText("You are using your phone alot");
                        Notification n = builder.build();

                        nm.notify(1, n);
                    }

                }
	        	getOpenedApps mThread = new getOpenedApps();
	        	mThread.start();


	        }
	        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
	        {
	        	screenOn=false;
	        	TimeUsed=System.currentTimeMillis()/1000-screenOnTimer;
	        	String duration="";
				if(TimeUsed<60)
					{if(TimeUsed>1)
						duration=TimeUsed+" seconds";
					else
						duration=TimeUsed+" second";
					}
				else
				{
					if(TimeUsed<3600)
					{
						
						int min=(int)TimeUsed/60;
						int sec=(int)TimeUsed-min*60;
						if(min>1)
						{
							if(sec>1)
								duration=min+" mins "+ sec+" secs";
							else
								duration=min+" mins "+ sec+" sec";

						}
						else
						{
							if(sec>1)
								duration=min+" min "+ sec+" secs";
							else
								duration=min+" min "+ sec+" sec";

						}
					 }
					else
						{ 
							
							int hrs=(int)TimeUsed/3600;
							int min=((int)TimeUsed-hrs*3600)/60;
							if(hrs>1)
							{
								if(min>1)
								{
									duration=hrs+" hrs "+ min+" mins";

								}
								else
								{
									duration=hrs+" hrs "+ min+" min";

								}
							}
							else
							{
								if(min>1)
								{
									duration=hrs+" hr "+ min+" mins";

								}
								else
								{
									duration=hrs+" hr "+ min+" min";

								}
							}

						}
				}
                DBAdapter db = new DBAdapter(context);
                db.open();
                Cursor c = db.getAllRecords();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat date = new SimpleDateFormat("dd MMMM yyyy" );
                startDate = date.format(calendar.getTime());
                if(c.moveToLast())
                {
                    if(!c.getString(2).equals(date.format(calendar.getTime())))
                    {
                        resetTimeUsed=true;
                        Log.d(""+c.getString(1),""+date.format(calendar.getTime())+"reset?"+resetTimeUsed);

                    }
                }
				if(startTime!=null)
				{
				mServiceIntent = new Intent(context, IntentServices.class);
				mServiceIntent.putExtra("UnlockTime",startTime);
				mServiceIntent.putExtra("UnlockDate",startDate);
				if(apps.isEmpty())
					mServiceIntent.putExtra("PackageName","");
				else
					mServiceIntent.putExtra("PackageName",apps.toString());		
				mServiceIntent.putExtra("TimeUsed",duration);
                mServiceIntent.putExtra("TimeUsedInMs",TimeUsed);
                mServiceIntent.putExtra("resetTimeUsed",resetTimeUsed);
                resetTimeUsed=false;
				context.startService(mServiceIntent);
				 
				final Timer t = new Timer();
				t.schedule(new TimerTask(){ 

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(intentServiceComplete==true){
						AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
				        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), Widget.class);
				        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
				        if (appWidgetIds != null && appWidgetIds.length > 0) {
				            onUpdate(context, appWidgetManager, appWidgetIds);
				        
						}
				        intentServiceComplete=false;

						}
					}}, 1000, 1000);
	        }
				else
				Log.e("SqLite", "Data Missing");
				
	        }
	
		}

        private void sendNotification(Context context,long timeUsed) {
            String message = "You are using your phone alot";
            Intent notificationIntent = new Intent(context, Start.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    1, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.notif)
                    .setTicker(message)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(message);
            Random r = new Random();
            int random =r.nextInt(3);
            switch (random) {
                case 3600:
                    builder.setContentText("Phone used for more than 1 hr.");
                    break;
                default:
                    builder.setContentText("Phone used for more than "+timeUsed/3600+" hrs.");
                    break;
            }


            Notification n = builder.build();
            nm.notify(1, n);
        }


    }
	
//Unregister the service when onDestroy  is called
@Override
public void onDestroy() {
	unregisterReceiver(mReceiver);
	super.onDestroy();
}

}