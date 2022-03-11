package org.processmining.filterbook.charts;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;

public class TraceChart {

	/**
	 * Returns a bar chart showing for every attribute value:
	 *   1. how many traces have that attribute value.
	 * 
	 * @param log The log.
	 * @param parameters The parameters.
	 * @return The panel containing the bar chart.
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
			AttributeValueType value = new AttributeValueType(
					trace.getAttributes().get(attribute.getAttribute().getKey()));
			values.add(value);
			if (counts.containsKey(value)) {
				counts.put(value, counts.get(value) + 1);
			} else {
				counts.put(value, 1);
			}
		}
		for (AttributeValueType value : values) {
			XAttribute a = value.getAttribute();
			if (a != null) {
				dataset.addValue(counts.get(value), attribute.getAttribute().getKey(), a.toString());
			} else {
				dataset.addValue(counts.get(value), attribute.getAttribute().getKey(),
						AttributeValueType.NOATTRIBUTEVALUE);
			}
		}
		JFreeChart chart = ChartUtils.createBarChart("Overview", attribute.getAttribute().getKey(),
				"Number of traces", dataset);

		return new ChartPanel(chart);
	}

}
