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

	public final static int YEAR_PRECISION = 1;
	public final static int MONTH_PRECISION = 2;
	public final static int DAY_PRECISION = 3;
	public final static int HOUR_PRECISION = 4;
	public final static int MINUTE_PRECISION = 5;
	public final static int SECOND_PRECISION = 6;
	public final static int MILLIS_PRECISION = 7;

	/**
	 * Create a duration type from a duration from the log.
	 * 
	 * @param duration
	 *            The duration from the log.
	 */
	public DurationType(Duration duration) {
		this(duration, MILLIS_PRECISION);
	}
	
	public DurationType(Duration duration, int precision) {
		this.duration = duration;
		long totalMillis = duration.toMillis();
		long seconds = totalMillis / 1000;

		long mYear = (long) Math.floor(seconds / 31536000);
		long mMonth = (long) Math.floor((seconds % 31536000) / 2628000);
		long mDay = (long) Math.floor(((seconds % 31536000) % 2628000) / 86400);
		long hr = (long) Math.floor(((seconds % 31536000) % 86400) / 3600);
		long min = (long) Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
		long sec = (((seconds % 31536000) % 86400) % 3600) % 60;
		long millis = totalMillis - seconds * 1000;

		durationString = "";
		String unit = "year";

		// this is done for a nicer display of the time
		// for the user
		// the UI should convert selected time to an
		// understandable representation
		if (precision >= YEAR_PRECISION) {
			durationString += addToDuration(mYear, "year");
		}
		if (precision >= MONTH_PRECISION) {
			durationString += addToDuration(mMonth, "month");
			unit = "month";
		}
		if (precision >= DAY_PRECISION) {
			durationString += addToDuration(mDay, "day");
			unit = "day";
		}
		if (precision >= HOUR_PRECISION) {
			durationString += addToDuration(hr, "hour");
			unit = "hour";
		}
		if (precision >= MINUTE_PRECISION) {
			durationString += addToDuration(min, "minute");
			unit = "minute";
		}
		if (precision >= SECOND_PRECISION) {
			durationString += addToDuration(sec, "second");
			unit = "second";
		}
		if (precision >= MILLIS_PRECISION) {
			durationString += addToDuration(millis, "millisecond");
			unit = "millisecond";
		}
		if (durationString.isEmpty()) {
			durationString = "less than 1 " + unit;
		}
	}

	/**
	 * Get the duration for this duration type.
	 * 
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

	private String addToDuration(long time, String type) {

		if (time == 0) {
			return "";
		} else if (time == 1) {
			return "1 " + type + " ";
		} else {
			return Long.toString(time) + " " + type + "s ";
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
