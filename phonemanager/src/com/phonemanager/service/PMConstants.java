package com.phonemanager.service;

public class PMConstants {

	public static final int TIME_FREQUENCY = 1000; //service activity check frequency in ms

	public static final int MAX_FLUSH_LIMIT=20;   //size of records to keep in cache

	public static final int MAX_TTL_FLUSH_TASK = 10000; //time to keep in cache

	public static final String SCREEN_LOCKED = "Screen Lock";

	public static final String HOME_SCREEN = "Home Screen";

	public static final String DATA_DELIMITER = ":";

	public static final String DB_NAME = "LogManager.db";

	public static final int DB_VERSION =1;

	public static final String DB_TABLE="ActivityData";

	public static final String DB_COL_ACTIVITY_NAME = "ACTIVITY_NAME";

	public static final String DB_COL_TIME_STAMP = "TIME_STAMP";

	public static final long THREE_DAY_SECOND = 72*60*60*1000L;

	
}
