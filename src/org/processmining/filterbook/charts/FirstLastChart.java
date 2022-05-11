package org.processmining.filterbook.charts;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.TableOrder;

public class FirstLastChart {

	/**
	 * Returns two pie charts showing the concept names for:
	 *   1. the first events in the log and
	 *   2. the last events in the log.
	 * 
	 * @param log The log.
	 * @param dummyClassifier The dummy classifier.
	 * @param parameters The parameters.
	 * @return The panel containing the pie charts.
	 */
	public static JComponent getChart(XLog log) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Set<String> firstValues = new TreeSet<String>();
		Set<String> lastValues = new TreeSet<String>();
		Map<String, Integer> firstCounts = new TreeMap<String, Integer>();
		Map<String, Integer> lastCounts = new TreeMap<String, Integer>();
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				String firstValue = XConceptExtension.instance().extractName(trace.get(0));
				firstValues.add(firstValue);
				if (firstCounts.containsKey(firstValue)) {
					firstCounts.put(firstValue,  firstCounts.get(firstValue) + 1);
				} else {
					firstCounts.put(firstValue, 1);
				}
				String lastValue = XConceptExtension.instance().extractName(trace.get(trace.size() - 1));
				lastValues.add(lastValue);
				if (lastCounts.containsKey(lastValue)) {
					lastCounts.put(lastValue,  lastCounts.get(lastValue) + 1);
				} else {
					lastCounts.put(lastValue, 1);
				}
			}
		}
		if (firstValues.size() > ChartUtils.TOO_MANY_VALUES || lastValues.size() > ChartUtils.TOO_MANY_VALUES) {
			return new JLabel("The chart contains too many different values to be shown");
		}
		for (String value : firstValues) {
			dataset.addValue(firstCounts.get(value), "First", value);
		}
		for (String value : lastValues) {
			dataset.addValue(lastCounts.get(value), "Last", value);
		}
		JFreeChart chart = ChartUtils.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW);

		return new ChartPanel(chart);
	}

}
