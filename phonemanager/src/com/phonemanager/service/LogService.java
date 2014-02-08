package com.phonemanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

	
public class LogService extends Service

{
	private static LogService instance = null;
	private TimerTask checkActivity;
	private Timer timer;
	private String lastActivity;
	private ActivityDataManager mDataManager;
	private KeyguardManager mKeyguardManager;
	private ActivityManager mActivityManager; 
	private PackageManager mPackageManager;
	private final IBinder mBinder = new LocalBinder(); 
	private String TAG = "LogService";
	public ArrayList<ActivityDataPair> dataCacheService;
	private Intent restartServiceIntent;
	private DataQueue localData;
	private int dataSize = 0;
	public class LocalBinder extends Binder
	{
		
		public LogService getService()
		{
			//return instance of this class to UI
			return LogService.this;
		}
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		//system initializations
		mDataManager = new ActivityDataManager(this.getBaseContext());
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mActivityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		mPackageManager  = getPackageManager();
		Log.d(TAG,"onCreate");
		//local reference initializations
		instance = this;
		checkActivity = new MyTimerTask();
		timer = new Timer();
		localData = new DataQueue();
		//timer.scheduleAtFixedRate(checkActivity, 0, PMConstants.TIME_FREQUENCY);
		lastActivity = "";
		
		
	}
	
	private static class DataQueue
	{
	    static ArrayList<ActivityDataPair> currentData;
		Long entry1;
		Long entry2;
		Long currentSysytemTimeStamp;
		Long threeDaysBeforeTimeStamp;
		ActivityDataPair localObj; 
		int i=0,size=0;
		
		public DataQueue()
		{
			currentData = new ArrayList<ActivityDataPair>();
			localObj = new ActivityDataPair();
		}
		
		public void addData(ActivityDataPair newData)
		{
			currentData.add(newData);
			Log.d("AddData at index","test");
			maintainArrayList();
		}
		public void viewData()
		{
			Log.d("ViewData at index","test");
		
			while(i<size)
			{
				localObj = currentData.get(i);
				Log.d("ViewData at index",""+i+"activity = "+localObj.getActivityName()+" time = "+localObj.getStartTime());
		
			}
		}
		public void maintainArrayList()
		{
			Log.d("maintainData at index","test");
			while(true)
			{
				entry1=currentData.get(0).getStartTime();
				if(!(currentData.size()>1))
					break;
				else
				{
				entry2=currentData.get(1).getStartTime();
				currentSysytemTimeStamp=normaliseTime((new Date()).getTime());
				threeDaysBeforeTimeStamp = currentSysytemTimeStamp - PMConstants.THREE_DAY_SECOND;
					if(entry1<threeDaysBeforeTimeStamp)
					{
						if(entry2<=threeDaysBeforeTimeStamp)
						{
						//remove activity at index 0
						currentData.remove(0);
						continue;
						}
					 
						else
						{
							//reset time of the index 0 activity
							localObj = currentData.get(0);
							localObj.setStartTime(currentSysytemTimeStamp);
							currentData.add(localObj);
							break;
						}
					}
				}
			}
		}
	}
	
	
	
	
	private class MyTimerTask extends TimerTask
	{
		
		public void run() 
		{
			
			String[] splitResultSet = getTopActivityStackTimeStampAndName().split(PMConstants.DATA_DELIMITER, 2); 
			
			// split result set will contain
			// [0] -> Time stamp on the activity
			// [1] -> Name of the activity
			
			long timeStamp = Long.parseLong(splitResultSet[0]);
			timeStamp = normaliseTime(timeStamp); //removing the milliseconds for easier calculations
			String currentActivity = splitResultSet[1];
			
			if(mKeyguardManager.isKeyguardLocked())
				currentActivity = PMConstants.SCREEN_LOCKED; 
			
			if(!lastActivity.equals(currentActivity)){
				ActivityDataPair newData = new ActivityDataPair(currentActivity, timeStamp);
				mDataManager.addData(newData);
				localData.addData(newData);
				lastActivity = currentActivity;
			}			
		}
	}
	
	/*
	private void dbinsert() throws RemoteException
	{
		// Content Provider
		Uri yourURI = Uri.parse("content://com.example.provider.MyContentProvider/table_three");
		ContentProviderClient yourCR = getContentResolver().acquireContentProviderClient(yourURI);
		ContentValues values = new ContentValues();
		values.put("ACTIVITYNAME", activityName);
		values.put("STARTTIME", toastTime);
		values.put("ENDTIME",toastTime+1);
		yourCR.insert(yourURI, values);
		Log.d("Db Insert logService ", "");
	}
*/
	public String getTopActivityStackTimeStampAndName() 
	{
		
		String packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		ApplicationInfo mApplicationInfo;
		try 
		{
			mApplicationInfo = mPackageManager.getApplicationInfo(packageName,0);
		} 
		catch (NameNotFoundException e)
		{
			mApplicationInfo = null;
		}
		String appName = (String) (mApplicationInfo != null ? mPackageManager.getApplicationLabel(mApplicationInfo) : "(unknown)");

		long timeStamp = (new Date()).getTime();
		return (timeStamp) + PMConstants.DATA_DELIMITER + appName;
	}

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		timer.scheduleAtFixedRate(checkActivity, 0, PMConstants.TIME_FREQUENCY);
		Log.d(TAG, "Received start id " + startId + ": " + intent+"inside onStartCommand");
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


	@Override
	public IBinder onBind(Intent intent) 
	{
		
		return mBinder;
	}

	@Override
	public void onDestroy()
	{
		restartServiceIntent = new Intent(this,LogService.class);
		Toast.makeText(getApplicationContext(),"Restarting Service from onDestroy",  Toast.LENGTH_SHORT).show();
		startService(restartServiceIntent);
		
		
	}
	private static long normaliseTime(long currentData) 
	{
		return (currentData / 1000L) * 1000L;
	}

	public static boolean isServiceInstance()
	{
		return instance != null;
		
	}
	
/*	public ArrayList<ActivityDataPair> fetch()
	{
		dataCacheService = mDataManager.dataCache;
		
		return dataCacheService;
	}*/
	
	public void fetchData(Handler h){
		if(h!=null){
			/*
			//send data for the DB query
			for(int i = 0 ; i < 10; i++){
				Message msg = new Message();
				msg.arg1 = i;
				msg.obj = new Date();
				h.sendMessage(msg);
			}
			*/
			//send data from class queue
			dataSize = DataQueue.currentData.size();
			for(int i = 0; i < dataSize; i++){
				String[] activityNameAndTime = new String[2];
				
				activityNameAndTime[0] = DataQueue.currentData.get(i).getActivityName();
				activityNameAndTime[1] = DataQueue.currentData.get(i).getStartTime().toString();
				Message msg1 = h.obtainMessage();
				msg1.obj = activityNameAndTime;
				h.sendMessage(msg1);
			}
			
			
			//now set a timer that will send data to this handler
			/*msg.arg1 = 24;
			h.sendMessage(msg);
			 String[] messageString = new String[2];
        Message message = handler.obtainMessage();
        messageString[0]="OK";
        messageString[1]="Number 1";
        message.obj = messageString;
        handler.sendMessage(message);
			*/
			
		}
			
	}
	
	/*private long incrementSecond(long timeStamp) 
	{
		return ((timeStamp / 1000L) + 1) * 1000L;
	}*/

	
}


