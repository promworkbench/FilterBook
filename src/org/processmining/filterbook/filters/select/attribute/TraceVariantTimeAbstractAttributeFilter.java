package org.processmining.filterbook.filters.select.attribute;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;

public abstract class TraceVariantTimeAbstractAttributeFilter extends TraceVariantAbstractAttributeFilter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select (using time) one trace for every variant";

	public TraceVariantTimeAbstractAttributeFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, log, parameters, cell);
	}

	public TraceVariantTimeAbstractAttributeFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, log, parameters, cell);
	}

	/**
	 * This filter is suitable if the log contains global attributes, at least one
	 * event, and time:timestamp as global event attribute.
	 */
	public boolean isSuitable() {
		if (!super.isSuitable()) {
			return false;
		}
		for (XAttribute globalAttribute : getLog().getGlobalEventAttributes()) {
			if (globalAttribute.getKey().equals(XTimeExtension.KEY_TIMESTAMP)) {
				return true;
			}
		}
		return false;
	}
}
