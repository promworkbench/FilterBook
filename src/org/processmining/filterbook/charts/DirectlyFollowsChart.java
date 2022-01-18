package org.processmining.filterbook.charts;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.TableOrder;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.collection.ComparablePair;

public class DirectlyFollowsChart {

	/**
	 * Returns for every event class a pie chart showing how many times it is directly followed 
	 * by every event class.
	 * 
	 * @param log The log.
	 * @param dummyClassifier The dummy classifier (in case no classifier has been selected).
	 * @param parameters The parameters.
	 * @return The panel containing the pie charts.
	 */
	public static JComponent getChart(XLog log, XEventClassifier dummyClassifier, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		/*
		 * Determine the classifier to use.
		 */
		XEventClassifier classifier = (parameters.getOneFromListClassifier().getSelected() != null
				? parameters.getOneFromListClassifier().getSelected().getClassifier()
				: dummyClassifier);

		/*
		 * Count the directly-follow pairs.
		 */
		Set<Pair<String, String>> values = new TreeSet<Pair<String, String>>();
		Map<Pair<String, String>, Integer> counts = new TreeMap<Pair<String, String>, Integer>();
		for (XTrace trace : log) {
			String prevValue = null;
			for (XEvent event : trace) {
				String value = classifier.getClassIdentity(event);
				if (prevValue != null) {
					/*
					 * Count the pair (prevValue, value).
					 */
					Pair<String, String> pair = new ComparablePair<String, String>(prevValue, value);
					values.add(pair);
					if (counts.containsKey(pair)) {
						counts.put(pair, counts.get(pair) + 1);
					} else {
						counts.put(pair, 1);
					}
				}
				prevValue = value;
			}
		}
		
		/*
		 * Create the data set from the counts.
		 */
		for (Pair<String, String> value : values) {
			dataset.addValue(counts.get(value), value.getFirst(), value.getSecond());
		}
		
		/*
		 * Create the chart and return a panel containing it.
		 * Note that by using TableOrder.BY_ROW we select to have a pie chart for every first element in the pair.
		 */
		JFreeChart chart = ChartFactory.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW, true, true,
				false);
		return new ChartPanel(chart);
	}
	
	/**
	 * Returns for every attribute value a pie chart showing how many times it is directly followed 
	 * by every attribute value.
	 * 
	 * @param log The log.
	 * @param parameters The parameters.
	 * @return The panel containing the pie charts.
	 */
	public static JComponent getChart(XLog log, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		/*
		 * Determine the attribute to use.
		 */
		AttributeType attribute;
		if (parameters.getOneFromListAttribute() == null
				|| parameters.getOneFromListAttribute().getSelected() == null) {
			attribute = new AttributeType(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""));
		} else {
			attribute = parameters.getOneFromListAttribute().getSelected();
		}

		/*
		 * Count the directly-follow pairs.
		 */
		Set<Pair<AttributeValueType, AttributeValueType>> values = new TreeSet<Pair<AttributeValueType, AttributeValueType>>();
		Map<Pair<AttributeValueType, AttributeValueType>, Integer> counts = new TreeMap<Pair<AttributeValueType, AttributeValueType>, Integer>();
		for (XTrace trace : log) {
			AttributeValueType prevValue = null;
			for (XEvent event : trace) {
				AttributeValueType value = new AttributeValueType(
						event.getAttributes().get(attribute.getAttribute().getKey()));
				if (prevValue != null) {
					/*
					 * Count the pair (prevValue, value).
					 */
					Pair<AttributeValueType, AttributeValueType> pair = new ComparablePair<AttributeValueType, AttributeValueType>(
							prevValue, value);
					values.add(pair);
					if (counts.containsKey(pair)) {
						counts.put(pair, counts.get(pair) + 1);
					} else {
						counts.put(pair, 1);
					}
				}
				prevValue = value;
			}
		}
		
		/*
		 * Create the data set. 
		 * Be aware of the possibility that an event may not have the attribute.
		 * If so, AttributeValueType.NOATTRIBUTEVALUE is used.
		 */
		for (Pair<AttributeValueType, AttributeValueType> value : values) {
			XAttribute a1 = value.getFirst().getAttribute();
			XAttribute a2 = value.getSecond().getAttribute();
			dataset.addValue(counts.get(value), (a1 != null ? a1.toString() : AttributeValueType.NOATTRIBUTEVALUE),
					(a2 != null ? a2.toString() : AttributeValueType.NOATTRIBUTEVALUE));
		}
		
		/*
		 * Create the chart and return a panel containing it.
		 */
		JFreeChart chart = ChartFactory.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW, true, true,
				false);
		return new ChartPanel(chart);
	}

}
