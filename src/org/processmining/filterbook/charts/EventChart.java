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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;

public class EventChart {

	public static JComponent getChart(XLog log, XEventClassifier dummyClassifer, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		XEventClassifier classifier = (parameters.getOneFromListClassifier().getSelected() != null
				? parameters.getOneFromListClassifier().getSelected().getClassifier()
				: dummyClassifer);

		Set<String> values = new TreeSet<String>();
		Map<String, Integer> counts = new TreeMap<String, Integer>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String value = classifier.getClassIdentity(event);
				values.add(value);
				if (counts.containsKey(value)) {
					counts.put(value,  counts.get(value) + 1);
				} else {
					counts.put(value, 1);
				}
			}
		}
		for (String value : values) {
			dataset.addValue(counts.get(value), classifier.name(), value);
		}
		JFreeChart chart = ChartFactory.createBarChart("Overview", classifier.name(), "Number of events",
				dataset, PlotOrientation.VERTICAL, false, true, false);
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

		Set<AttributeValueType> values = new TreeSet<AttributeValueType>();
		Map<AttributeValueType, Integer> counts = new TreeMap<AttributeValueType, Integer>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				AttributeValueType value = new AttributeValueType(event.getAttributes().get(attribute.getAttribute().getKey()));
				values.add(value);
				if (counts.containsKey(value)) {
					counts.put(value,  counts.get(value) + 1);
				} else {
					counts.put(value, 1);
				}
			}
		}
		for (AttributeValueType value : values) {
			XAttribute a = value.getAttribute();
			if (a != null) {
				dataset.addValue(counts.get(value), attribute.getAttribute().getKey(), a.toString());
			} else {
				dataset.addValue(counts.get(value), attribute.getAttribute().getKey(), AttributeValueType.NOATTRIBUTEVALUE);
			}
		}
		JFreeChart chart = ChartFactory.createBarChart("Overview", attribute.getAttribute().getKey(), "Number of events",
				dataset, PlotOrientation.VERTICAL, false, true, false);
		return new ChartPanel(chart);
	}

}
