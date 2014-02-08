package com.phonemanager.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phonemanager.R;
import com.phonemanager.service.LogService;
import com.phonemanager.service.LogService.LocalBinder;

//import com.logmanager.logservice.LocalBinder;

public class MainActivity extends Activity implements ServiceConnection,
		OnClickListener {
	// private List<String> tasks_list = new ArrayList<String>();
	private TextView dataPanel;
	private Button taskbutton, displaybutton;
	private Intent serviceIntent;
	String tasks;
	boolean isService;
	private LogService serviceObj;
	private HashMap<String, ArrayList<Long>> uiDataMap;

	private Handler serviceResponse = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Gets the image task from the incoming Message object.
			String[] service = (String[]) msg.obj;
			Log.d("Result :", "Activity Name" + service[0] + "Time"
					+ service[1]);

			Toast.makeText(getApplicationContext(),
					"" + service[0] + " " + service[1], Toast.LENGTH_SHORT)
					.show();

			/*
			 * if(msg.obj!=null) Log.d("Result :", "Object : " +
			 * ((Date)msg.obj).toString());
			 */

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dataPanel = (TextView) findViewById(R.id.data_panel);
		// taskbutton = (Button) findViewById(R.id.running_task);
		displaybutton = (Button) findViewById(R.id.show);
		Log.d("TAG", "::::::oncreate of MainActivity:::::::::");
		// taskbutton.setOnClickListener(this);
		displaybutton.setOnClickListener(this);
		// uiDataMap = new HashMap<String, ArrayList<Long>>();

		// bind service
		boolean isService = false;
		isService = LogService.isServiceInstance();
		serviceIntent = new Intent(MainActivity.this, LogService.class);
		// serviceIntent.setClassName("com.example.logmanagerservice",
		// "com.example.logmanagerservice.LogService");
		// startService(serviceIntent);
		if (!isService) {
			startService(serviceIntent);
			Log.d("TAG", "::::::inside isService:::::::::");
		}

		Log.d("TAG", "::::::before bindService:::::::::");
		isService = bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
		Log.d("TAG", "::::::after bindService:::::::::isService=" + isService);

		/*
		 * new Thread() { public void run() {
		 * 
		 * } }.start();
		 */

	}

	/*
	 * private String getActivityNames() { Context context =
	 * this.getApplicationContext(); ActivityManager mgr =
	 * (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
	 * 
	 * ConfigurationInfo config = mgr.getDeviceConfigurationInfo();
	 * List<RunningAppProcessInfo> processes = mgr.getRunningAppProcesses();
	 * List<RunningTaskInfo> tasks = mgr.getRunningTasks(100);
	 * 
	 * String text = "";
	 * 
	 * text += "Running tasks: \n"; for(Iterator<RunningTaskInfo> i =
	 * tasks.iterator(); i.hasNext(); ) { RunningTaskInfo p =
	 * (RunningTaskInfo)i.next(); text += p.baseActivity.flattenToString() +
	 * "\n"; tasks_list.add(p.baseActivity.flattenToString()); } //
	 * activityMap.put(text,10L); return text; }
	 */

	/*
	 * private void displayUIMap() { //code to display the contents of the data
	 * map //onto the dataPanel text view
	 * 
	 * Iterator mapIterator = uiDataMap.entrySet().iterator();
	 * dataPanel.setText(""); StringBuilder str = new StringBuilder();
	 * while(mapIterator.hasNext()) { HashMap.Entry dataSet =
	 * (HashMap.Entry)mapIterator.next(); String activityName =
	 * (String)dataSet.getKey(); ArrayList<Long> timeValues =
	 * (ArrayList<Long>)dataSet.getValue(); int
	 * number_of_strings=timeValues.size(); str.append(activityName+"\n");
	 * str.append(number_of_strings+"\n\n"); } dataPanel.setText(str); }
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.d("TAG", "call to onserviceConnected");
		serviceObj = ((LocalBinder) service).getService();
		serviceObj.fetchData(serviceResponse);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		serviceObj = null;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.d("ONCLICK", "" + isService);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(this);
		Toast.makeText(getApplicationContext(), "16. onDestroy()",
				Toast.LENGTH_SHORT).show();
	}

}
