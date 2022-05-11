package org.processmining.filterbook.charts;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.TableOrder;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;

public class LastChart {

	/**
	 * Returns a pie chart showing the event classes for the last events in the log.
	 * 
	 * @param log The log.
	 * @param dummyClassifier The dummy classifier.
	 * @param parameters The parameters.
	 * @return The panel containing the pie chart.
	 */
	public static JComponent getChart(XLog log, XEventClassifier dummyClassifier, Parameters parameters) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		XEventClassifier classifier = (parameters.getOneFromListClassifier().getSelected() != null
				? parameters.getOneFromListClassifier().getSelected().getClassifier()
				: dummyClassifier);

		Set<String> values = new TreeSet<String>();
		Map<String, Integer> counts = new TreeMap<String, Integer>();
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				String value = classifier.getClassIdentity(trace.get(trace.size() - 1));
				values.add(value);
				if (counts.containsKey(value)) {
					counts.put(value,  counts.get(value) + 1);
				} else {
					counts.put(value, 1);
				}
			}
		}
		if (values.size() > ChartUtils.TOO_MANY_VALUES) {
			return new JLabel("The chart contains too many different values to be shown");
		}
		for (String value : values) {
			dataset.addValue(counts.get(value), classifier.name(), value);
		}
		JFreeChart chart = ChartUtils.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW);

		return new ChartPanel(chart);
	}
	
	/**
	 * Returns a pie chart showing the attribute values for the last events in the log.
	 * 
	 * @param log The log.
	 * @param parameters The parameters.
	 * @return The panel containing the pie chart.
	 */
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
			if (!trace.isEmpty()) {
				AttributeValueType value = new AttributeValueType(trace.get(trace.size() - 1).getAttributes().get(attribute.getAttribute().getKey()));
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
		JFreeChart chart = ChartUtils.createMultiplePieChart("Overview", dataset, TableOrder.BY_ROW);

		return new ChartPanel(chart);
	}

}
