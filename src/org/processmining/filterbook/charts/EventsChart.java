package org.processmining.filterbook.charts;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;

public class EventsChart {

	/**
	 * Returns a bar chart showing for every (classifier-based) variant:
	 *   1. how many times this variant occurs in the log.
	 * 
	 * @param occurrences For every variant the number of times it occurs in the log.
	 * @param dummyClassifier The dummy classifier.
	 * @param parameters The parameters.
	 * @return The panel containing the bar chart.
	 */
	public static JComponent getChart(Map<List<String>, Integer> occurrences, XEventClassifier dummyClassifier, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		XEventClassifier classifier = (parameters.getOneFromListClassifier().getSelected() != null
				? parameters.getOneFromListClassifier().getSelected().getClassifier()
				: dummyClassifier);

		TreeSet<Integer> values = new TreeSet<Integer>(occurrences.values());
		for (Integer v : values) {
			for (List<String> value : occurrences.keySet()) {
				if (occurrences.get(value).equals(v)) {
					dataset.addValue(occurrences.get(value), classifier.name(), value.toString());
				}
			}
		}
		JFreeChart chart = ChartFactory.createBarChart("Overview", classifier.name(), "Number of traces", dataset,
				PlotOrientation.HORIZONTAL, false, true, false);
		return new ChartPanel(chart);
	}
	
	/**
	 * Returns a bar chart showing for every (attribute-based) variant:
	 *   1. how many times this variant occurs in the log.
	 * 
	 * @param occurrences For every variant the number of times it occurs in the log.
	 * @param parameters The parameters.
	 * @return The panel containing the bar chart.
	 */
	public static JComponent getChart(Map<List<String>, Integer> occurrences, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		AttributeType attribute;
		if (parameters.getOneFromListAttribute() == null
				|| parameters.getOneFromListAttribute().getSelected() == null) {
			attribute = new AttributeType(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""));
		} else {
			attribute = parameters.getOneFromListAttribute().getSelected();
		}

		TreeSet<Integer> values = new TreeSet<Integer>(occurrences.values());
		for (Integer v : values) {
			for (List<String> value : occurrences.keySet()) {
				if (occurrences.get(value).equals(v)) {
					dataset.addValue(occurrences.get(value), attribute.getAttribute().getKey(), value.toString());
				}
			}
		}
		JFreeChart chart = ChartFactory.createBarChart("Overview", attribute.getAttribute().getKey(), "Number of traces", dataset,
				PlotOrientation.HORIZONTAL, false, true, false);
		return new ChartPanel(chart);
	}

}
