package org.processmining.filterbook.charts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class DateChart {

	/**
	 * Returns a bar chart showing for every abstract date in the log: 1. how
	 * many first events have that abstract date and 2. how many last events
	 * have that abstract date. Based on the time span of the log, the
	 * abstraction and the time unit (days, hours, ...) is selected
	 * automatically.
	 * 
	 * @param log
	 *            The log.
	 * @param firstLogDate
	 *            The earliest first date in the log.
	 * @param lastLogDate
	 *            The latest last date in the log.
	 * @return The panel containing the bar chart.
	 */
	public static JComponent getChart(XLog log, Date firstLogDate, Date lastLogDate) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		/*
		 * Ordered sets of first and last abstract dates.
		 */
		Set<String> firstValues = new TreeSet<String>();
		Set<String> lastValues = new TreeSet<String>();

		/*
		 * Counts for this and last abstract dates.
		 */
		Map<String, Integer> firstCounts = new TreeMap<String, Integer>();
		Map<String, Integer> lastCounts = new TreeMap<String, Integer>();

		/*
		 * Determine the abstraction and time unit to use based on the time span
		 * of the log.
		 */
		long duration = lastLogDate.getTime() - firstLogDate.getTime();
		SimpleDateFormat dateFormat; // Used as an abstraction for the time unit.
		String units;
		if (duration < 1000) {
			// Less than a second. Use millis.
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			units = "Millis";
		} else if (duration < 600000L) { // 10 minutes
			// More than a second but less than 10 minutes. Use seconds.
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			units = "Seconds";
		} else if (duration < 36000000L) { // 10 hours
			// More than 10 minutes but less than 10 hours. Use minutes.
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			units = "Minutes";
		} else if (duration < 864000000L) { // 10 days
			// More than 10 hours but less than 10 days. Use hours.
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH");
			units = "Hours";
		} else if (duration < 25920000000L) { // about 10 months
			// More than 10 days, bu tless than (about) 10 months. Use days.
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			units = "Days";
		} else if (duration < 311040000000L) { // about 10 years
			// more than (about) 10 months but less than (about) 10 yeas. Use months.
			dateFormat = new SimpleDateFormat("yyyy-MM");
			units = "Months";
		} else {
			// More than 10 years. Use years.
			dateFormat = new SimpleDateFormat("yyyy");
			units = "Years";
		}

		/*
		 * Now the abstraction is known, count the first and last abstract
		 * dates.
		 */
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				String firstValue = dateFormat.format(XTimeExtension.instance().extractTimestamp(trace.get(0)));
				firstValues.add(firstValue);
				if (firstCounts.containsKey(firstValue)) {
					firstCounts.put(firstValue, firstCounts.get(firstValue) + 1);
				} else {
					firstCounts.put(firstValue, 1);
				}
				String lastValue = dateFormat
						.format(XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)));
				lastValues.add(lastValue);
				if (lastCounts.containsKey(lastValue)) {
					lastCounts.put(lastValue, lastCounts.get(lastValue) + 1);
				} else {
					lastCounts.put(lastValue, 1);
				}
			}
		}

		if (firstValues.size() > ChartUtils.TOO_MANY_VALUES || lastValues.size() > ChartUtils.TOO_MANY_VALUES) {
			return new JLabel("The chart contains too many different values to be shown");
		}
		/*
		 * Add the counts as series to the data set.
		 */
		for (String value : firstValues) {
			dataset.addValue(firstCounts.get(value), "First date", value);
		}
		for (String value : lastValues) {
			dataset.addValue(lastCounts.get(value), "Last date", value);
		}

		/*
		 * Create the chart, and return a panel containing it.
		 */
		JFreeChart chart = ChartUtils.createBarChart("Overview", units, "Number of events", dataset);

		return new ChartPanel(chart);
	}

}
