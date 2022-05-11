package org.processmining.filterbook.charts;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class LengthChart {

	/**
	 * Returns a bar chart showing for every trace length: 1. how many traces
	 * have that length.
	 * 
	 * @param log
	 *            The log.
	 * @return The panel containing the chart.
	 */
	public static JComponent getChart(XLog log) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int maxValue = Integer.MIN_VALUE;
		int minValue = Integer.MAX_VALUE;
		for (XTrace trace : log) {
			int value = trace.size();
			maxValue = Math.max(maxValue, value);
			minValue = Math.min(minValue, value);
		}
		double values[] = new double[maxValue + 1 - minValue];
		for (XTrace trace : log) {
			int length = trace.size();
			values[length - minValue]++;
		}
		if (maxValue - minValue > ChartUtils.TOO_MANY_VALUES) {
			return new JLabel("The chart contains too many different values to be shown");
		}

		for (int i = minValue; i <= maxValue; i++) {
			dataset.addValue(values[i - minValue], "Number of traces", String.valueOf(i));
		}
		JFreeChart chart = ChartUtils.createBarChart("Overview of trace lengths", "Trace length", "Number of traces",
				dataset);

		return new ChartPanel(chart);
	}
}
