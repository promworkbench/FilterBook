package org.processmining.filterbook.charts;

import java.time.Duration;
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
import org.processmining.filterbook.types.DurationType;

public class DurationChart {

	/**
	 * Returns a bar chart showing for every trace length: 1. how many traces
	 * have that length.
	 * 
	 * @param log
	 *            The log.
	 * @return The panel containing the chart.
	 */
	public static JComponent getChart(XLog log, int precision) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Set<DurationType> values = new TreeSet<DurationType>();
		Map<DurationType, Integer> counts = new TreeMap<DurationType, Integer>();
		for (XTrace trace : log) {
			DurationType value = new DurationType(
					Duration.between(XTimeExtension.instance().extractTimestamp(trace.get(0)).toInstant(),
							XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)).toInstant()),
					precision);
			values.add(value);
			if (counts.containsKey(value)) {
				counts.put(value, counts.get(value) + 1);
			} else {
				counts.put(value, 1);
			}
		}
		for (DurationType value : values) {
			dataset.addValue(counts.get(value), "Duration", value.toString());
		}
		JFreeChart chart = ChartFactory.createBarChart("Overview", "Duration", "Number of traces", dataset,
				PlotOrientation.HORIZONTAL, false, true, false);
		return new ChartPanel(chart);
	}
}
