package com.phonemanager.service;

public class ActivityDataPair {
private String activityName;
private Long startTime;

public ActivityDataPair(){
	this("",0L);
}

public ActivityDataPair(String activityName, Long startTime){
	this.activityName = activityName;
	this.startTime = startTime;
}

public void setActivityName(String activityName){
	this.activityName = activityName;
}

public void setStartTime(Long startTime){
	this.startTime = startTime;
}

public String getActivityName(){
	return activityName;
}

public Long getStartTime(){
	return startTime;
}

}
