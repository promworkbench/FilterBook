package org.processmining.filterbook.charts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class DateChart {

	/**
	 * Returns a bar chart showing the dates of all first events and last events in the log.
	 * Based on the time span of the log, a time unit (days, hours, ...) is selected automatically.
	 * 
	 * @param log The log.
	 * @param firstLogDate The earliest date in the log.
	 * @param lastLogDate The latest date in the log.
	 * @return The chart.
	 */
	public static JComponent getChart(XLog log, Date firstLogDate, Date lastLogDate) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Set<String> firstValues = new TreeSet<String>();
		Set<String> lastValues = new TreeSet<String>();
		Map<String, Integer> firstCounts = new TreeMap<String, Integer>();
		Map<String, Integer> lastCounts = new TreeMap<String, Integer>();
		long duration = lastLogDate.getTime() - firstLogDate.getTime();
		SimpleDateFormat dateFormat;
		String units;
		if (duration < 1000) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			units = "Millis";
		} else if (duration < 600000L) { // 10 minutes
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			units = "Seconds";
		} else if (duration < 36000000L) { // 10 hours
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			units = "Minutes";
		} else if (duration < 864000000L) { // 10 days
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH");
			units = "Hours";
		} else if (duration < 25920000000L) { // about 10 months
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			units = "Days";
		} else if (duration < 311040000000L) { // about 10 years
			dateFormat = new SimpleDateFormat("yyyy-MM");
			units = "Months";
		} else {
			dateFormat = new SimpleDateFormat("yyyy");
			units = "Years";
		}
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				String firstValue = dateFormat.format(XTimeExtension.instance().extractTimestamp(trace.get(0)));
				firstValues.add(firstValue);
				if (firstCounts.containsKey(firstValue)) {
					firstCounts.put(firstValue,  firstCounts.get(firstValue) + 1);
				} else {
					firstCounts.put(firstValue, 1);
				}
				String lastValue = dateFormat.format(XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)));
				lastValues.add(lastValue);
				if (lastCounts.containsKey(lastValue)) {
					lastCounts.put(lastValue,  lastCounts.get(lastValue) + 1);
				} else {
					lastCounts.put(lastValue, 1);
				}
			}
		}
		for (String value : firstValues) {
			dataset.addValue(firstCounts.get(value), "First date", value);
		}
		for (String value : lastValues) {
			dataset.addValue(lastCounts.get(value), "Last date", value);
		}
//		JFreeChart chart = ChartFactory.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW, true, true,
//				false);
		JFreeChart chart = ChartFactory.createBarChart("Overview", units, "Number of events",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		return new ChartPanel(chart);
	}

}
