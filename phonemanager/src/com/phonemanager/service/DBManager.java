package com.phonemanager.service;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/*
 * @author Prateek Negi
 * 
 * This is a singleton class for handling DataBase operations
 * Call to write and read data here
 * Note : Caching may be implemented here later for optimizations
 */

public class DBManager extends SQLiteOpenHelper {
	private static DBManager dbManager;
	private ArrayList<ActivityDataPair> expectedDataFormat;
	private String TAG = "DBManager";
	private String createTableQuery;

	private DBManager(Context context) {
		super(context, PMConstants.DB_NAME, null, PMConstants.DB_VERSION);

		// need to check this function
		getWritableDatabase();
		expectedDataFormat = new ArrayList<ActivityDataPair>();
		Log.d(TAG, "Constructor() call completed");
	}

	public static DBManager getInstance(Context context) {
		if (dbManager == null)
			dbManager = new DBManager(context);
		return dbManager;
	}

	public void write(final ArrayList<ActivityDataPair> dataSet) {
		expectedDataFormat.addAll(dataSet);
		SQLiteDatabase mSqliteObject = this.getWritableDatabase();
		Iterator<ActivityDataPair> dataIterator = expectedDataFormat.iterator();
		String INSERT_QUERY = "INSERT INTO " + PMConstants.DB_TABLE + " ("
		+ PMConstants.DB_COL_ACTIVITY_NAME + ","
		+ PMConstants.DB_COL_TIME_STAMP + ") VALUES (?,?);";

		final SQLiteStatement statement = mSqliteObject
		.compileStatement(INSERT_QUERY);

		mSqliteObject.beginTransaction();

		Log.d(TAG, "Starting record Transaction");

		try {
			while (dataIterator.hasNext()) {
				statement.clearBindings();
				ActivityDataPair currentPair = dataIterator.next();
				statement.bindString(1, currentPair.getActivityName());
				statement.bindLong(2, currentPair.getStartTime());
				statement.execute();
				Log.d(TAG, "Statement Execute complete");
			}
			mSqliteObject.setTransactionSuccessful();
		} finally {
			mSqliteObject.endTransaction();
			Log.d(TAG, "End of Record Transaction");
		}

		expectedDataFormat.clear();
	}

	public Cursor read(long startTime) {

		String TIME_STRING = String.valueOf(startTime);
		SQLiteDatabase mSqliteObject = this.getReadableDatabase();
		Cursor mCursor = mSqliteObject.rawQuery("SELECT * FROM "
		+ PMConstants.DB_NAME + " WHERE " + PMConstants.DB_COL_TIME_STAMP
				+ ">?",
		new String[] { TIME_STRING });
		return mCursor;
	}

	@Override
	public void onCreate(SQLiteDatabase mSqliteObject) {
		Log.d(TAG, "onCreate call placed");
		createTableQuery = "CREATE TABLE IF NOT EXISTS " + PMConstants.DB_TABLE
		+ "(" + PMConstants.DB_COL_ACTIVITY_NAME + " TEXT,"
		+ PMConstants.DB_COL_TIME_STAMP + " INTEGER);";
		mSqliteObject.execSQL(createTableQuery);
		Log.d(TAG, "Table create call placed");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
