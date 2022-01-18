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
	
	public static JComponent getChart(XLog log, XEventClassifier dummyClassifier, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		XEventClassifier classifier = (parameters.getOneFromListClassifier().getSelected() != null
				? parameters.getOneFromListClassifier().getSelected().getClassifier()
				: dummyClassifier);

		Set<Pair<String, String>> values = new TreeSet<Pair<String, String>>();
		Map<Pair<String, String>, Integer> counts = new TreeMap<Pair<String, String>, Integer>();
		for (XTrace trace : log) {
			String prevValue = null;
			for (XEvent event : trace) {
				String value = classifier.getClassIdentity(event);
				if (prevValue != null) {
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
		for (Pair<String, String> value : values) {
			dataset.addValue(counts.get(value), value.getFirst(), value.getSecond());
		}
		JFreeChart chart = ChartFactory.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW, true, true,
				false);
		return new ChartPanel(chart);
	}
	
	public static JComponent getChart(XLog log, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		AttributeType attribute;
		if (parameters.getOneFromListAttribute() == null
				|| parameters.getOneFromListAttribute().getSelected() == null) {
			attribute = new AttributeType(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""));
		} else {
			attribute = parameters.getOneFromListAttribute().getSelected();
		}

		Set<Pair<AttributeValueType, AttributeValueType>> values = new TreeSet<Pair<AttributeValueType, AttributeValueType>>();
		Map<Pair<AttributeValueType, AttributeValueType>, Integer> counts = new TreeMap<Pair<AttributeValueType, AttributeValueType>, Integer>();
		for (XTrace trace : log) {
			AttributeValueType prevValue = null;
			for (XEvent event : trace) {
				AttributeValueType value = new AttributeValueType(
						event.getAttributes().get(attribute.getAttribute().getKey()));
				if (prevValue != null) {
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
		for (Pair<AttributeValueType, AttributeValueType> value : values) {
			XAttribute a1 = value.getFirst().getAttribute();
			XAttribute a2 = value.getSecond().getAttribute();
			dataset.addValue(counts.get(value), (a1 != null ? a1.toString() : AttributeValueType.NOATTRIBUTEVALUE),
					(a2 != null ? a2.toString() : AttributeValueType.NOATTRIBUTEVALUE));
		}
		JFreeChart chart = ChartFactory.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW, true, true,
				false);
		return new ChartPanel(chart);
	}

}
