package com.phonemanager.service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

public class ActivityDataManager {
	private ArrayList<ActivityDataPair> dataCache;
	private DBManager mDBHandler;
	private Timer flushTimer;
	private CountDownTimerTask flushTask;
	
	public ActivityDataManager(Context mContext){
		dataCache = new ArrayList<ActivityDataPair>();
		mDBHandler = DBManager.getInstance(mContext);
		flushTask = new CountDownTimerTask();
		resetTimer();
		
	}
	
	private void resetTimer(){
		if(flushTimer != null)
			flushTimer.cancel();
		flushTimer = new Timer();
		flushTimer.scheduleAtFixedRate(flushTask, 0, PMConstants.MAX_TTL_FLUSH_TASK);
	}
	
	public void addData(final ActivityDataPair newDataPair){
		dataCache.add(newDataPair);
		checkForDump();
	}
	
	private void checkForDump(){
		if(dataCache.size() >= PMConstants.MAX_FLUSH_LIMIT){
			saveData();
			resetTimer();
		}
	}
	
	private void saveData(){
		if(dataCache.size() >= 0){
			mDBHandler.write(dataCache);
			dataCache.clear();
		}
	}
	
	public void flush(){
		saveData();
		resetTimer();
	}
	
	private class CountDownTimerTask extends TimerTask{
		@Override
		public void run(){
			saveData();
		}
	}
}
