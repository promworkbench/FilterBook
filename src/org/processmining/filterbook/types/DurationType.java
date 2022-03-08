package org.processmining.filterbook.types;

import java.time.Duration;

public class DurationType implements Comparable<DurationType> {

	/*
	 * Wrapper class for durations.
	 */
	
	/**
	 * The duration for this type. Never null.
	 */
	private Duration duration;
	
	private String durationString;
	
	/**
	 * Create a duration type from a duration from the log.
	 * @param duration The duration from the log.
	 */
	public DurationType(Duration duration) {
		this.duration = duration;
		long totalMillis = duration.toMillis();
		int seconds = (int) (totalMillis/1000);
		
		int mYear = (int) Math.floor(seconds / 31536000);
		int mMonth = (int) Math.floor((seconds % 31536000) / 2628000);
		int mDay = (int) Math.floor(((seconds % 31536000) % 2628000)/ 86400); 
		int hr = (int) Math.floor(((seconds % 31536000) % 86400) / 3600);
		int min = (int) Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
		int sec = (((seconds % 31536000) % 86400) % 3600) % 60;
		int millis = (int) (totalMillis - seconds*1000);
		
		durationString = "";
		
		// this is done for a nicer display of the time
		// for the user
		// the UI should convert selected time to an
		// understandable representation
		durationString += addToDuration(mYear, "year");
		durationString += addToDuration(mMonth, "month");
		durationString += addToDuration(mDay, "day");
		durationString += addToDuration(hr, "hour");
		durationString += addToDuration(min, "minute");
		durationString += addToDuration(sec, "second");
		durationString += addToDuration(millis, "millisecond");
	}
	
	/**
	 * Get the duration for this duration type.
	 * @return THe duration for this duration type.
	 */
	public Duration getDuration() {
		return duration;
	}
	
	/**
	 * Name for the duration type. Use duration string.
	 */
	public String toString() {
		return durationString;
	}
	
	private String addToDuration(int time, String type) {

		if (time == 0) {
			return "";
		} else if (time == 1) {
			return "1 " + type + " ";
		} else {
			return Integer.toString(time) + " " + type + "s ";
		}

	}

	/**
	 * Make the duration type comparable. 
	 */
	public int compareTo(DurationType o) {
		if (durationString.equals(o.durationString)) {
			return 0;
		}
		return duration.compareTo(o.duration);
	}
	
	public boolean equals(Object o) {
		if (o instanceof DurationType) {
			return durationString.equals(((DurationType) o).durationString);
		}
		return false;
	}
	
	public int hashCode() {
		return durationString.hashCode();
 	}
}
