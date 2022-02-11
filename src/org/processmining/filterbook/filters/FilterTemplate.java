package org.processmining.filterbook.filters;

import org.apache.commons.lang3.StringEscapeUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.framework.util.HTMLToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FilterTemplate implements HTMLToString {

	private String name;
	private ParametersTemplate parameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParametersTemplate getParameters() {
		return parameters;
	}

	public void setParameters(ParametersTemplate parameters) {
		this.parameters = parameters;
	}

	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buf = new StringBuffer();
		if (includeHTMLTags) {
			buf.append("<html>");
		}
		buf.append("<li>Filter<ol>");
		buf.append("<li>Name: " + StringEscapeUtils.escapeHtml4(getName()) + "</li>");
		buf.append("<li>Parameters: <ol>");
		buf.append(parameters.toHTMLString(false));
		buf.append("</ol></li>");
		buf.append("</ol></li>");
		if (includeHTMLTags) {
			buf.append("</html>");
		}
		return buf.toString();
	}

	public Filter createFilter(XLog log, ComputationCell cell) {
		if (name.equals(EventAttributeFilter.class.getName())) {
			EventAttributeFilter filter = new EventAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventClassifierFilter.class.getName())) {
			EventClassifierFilter filter = new EventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(EventFirstEventAttributeFilter.class.getName())) {
			EventFirstEventAttributeFilter filter = new EventFirstEventAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstEventAttributeTraceFilter.class.getName())) {
			EventFirstEventAttributeTraceFilter filter = new EventFirstEventAttributeTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstEventGlobalAttributeFilter.class.getName())) {
			EventFirstEventGlobalAttributeFilter filter = new EventFirstEventGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstEventGlobalAttributeTraceFilter.class.getName())) {
			EventFirstEventGlobalAttributeTraceFilter filter = new EventFirstEventGlobalAttributeTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstEventClassifierFilter.class.getName())) {
			EventFirstEventClassifierFilter filter = new EventFirstEventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstEventClassifierTraceFilter.class.getName())) {
			EventFirstEventClassifierTraceFilter filter = new EventFirstEventClassifierTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(EventFirstLastEventAttributeFilter.class.getName())) {
			EventFirstLastEventAttributeFilter filter = new EventFirstLastEventAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventAttributeTraceFilter.class.getName())) {
			EventFirstLastEventAttributeTraceFilter filter = new EventFirstLastEventAttributeTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventGlobalAttributeFilter.class.getName())) {
			EventFirstLastEventGlobalAttributeFilter filter = new EventFirstLastEventGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventGlobalAttributeTraceFilter.class.getName())) {
			EventFirstLastEventGlobalAttributeTraceFilter filter = new EventFirstLastEventGlobalAttributeTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventClassifierFilter.class.getName())) {
			EventFirstLastEventClassifierFilter filter = new EventFirstLastEventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventClassifierTraceFilter.class.getName())) {
			EventFirstLastEventClassifierTraceFilter filter = new EventFirstLastEventClassifierTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(EventGlobalAttributeFilter.class.getName())) {
			EventGlobalAttributeFilter filter = new EventGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(EventLastEventAttributeFilter.class.getName())) {
			EventLastEventAttributeFilter filter = new EventLastEventAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventLastEventAttributeTraceFilter.class.getName())) {
			EventLastEventAttributeTraceFilter filter = new EventLastEventAttributeTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventLastEventGlobalAttributeFilter.class.getName())) {
			EventLastEventGlobalAttributeFilter filter = new EventLastEventGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventLastEventGlobalAttributeTraceFilter.class.getName())) {
			EventLastEventGlobalAttributeTraceFilter filter = new EventLastEventGlobalAttributeTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventLastEventClassifierFilter.class.getName())) {
			EventLastEventClassifierFilter filter = new EventLastEventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventLastEventClassifierTraceFilter.class.getName())) {
			EventLastEventClassifierTraceFilter filter = new EventLastEventClassifierTraceFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(LogGlobalsFilter.class.getName())) {
			LogGlobalsFilter filter = new LogGlobalsFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceAttributeFilter.class.getName())) {
			TraceAttributeFilter filter = new TraceAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceFirstEventClassifierFilter.class.getName())) {
			TraceFirstEventClassifierFilter filter = new TraceFirstEventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceFirstEventGlobalAttributeFilter.class.getName())) {
			TraceFirstEventGlobalAttributeFilter filter = new TraceFirstEventGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceFirstLastEventFilter.class.getName())) {
			TraceFirstLastEventFilter filter = new TraceFirstLastEventFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceGlobalAttributeFilter.class.getName())) {
			TraceGlobalAttributeFilter filter = new TraceGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceLastEventClassifierFilter.class.getName())) {
			TraceLastEventClassifierFilter filter = new TraceLastEventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceLastEventGlobalAttributeFilter.class.getName())) {
			TraceLastEventGlobalAttributeFilter filter = new TraceLastEventGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(TraceLengthFilter.class.getName())) {
			TraceLengthFilter filter = new TraceLengthFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(TraceOccurrencesAttributeFilter.class.getName())) {
			TraceOccurrencesAttributeFilter filter = new TraceOccurrencesAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceOccurrencesGlobalAttributeFilter.class.getName())) {
			TraceOccurrencesGlobalAttributeFilter filter = new TraceOccurrencesGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceOccurrencesClassifierFilter.class.getName())) {
			TraceOccurrencesClassifierFilter filter = new TraceOccurrencesClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(EventOccurrencesAttributeFilter.class.getName())) {
			EventOccurrencesAttributeFilter filter = new EventOccurrencesAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventOccurrencesGlobalAttributeFilter.class.getName())) {
			EventOccurrencesGlobalAttributeFilter filter = new EventOccurrencesGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventOccurrencesClassifierFilter.class.getName())) {
			EventOccurrencesClassifierFilter filter = new EventOccurrencesClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(TraceLogFilter.class.getName())) {
			TraceLogFilter filter = new TraceLogFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceUniqueNameFilter.class.getName())) {
			TraceUniqueNameFilter filter = new TraceUniqueNameFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceSampleFilter.class.getName())) {
			TraceSampleFilter filter = new TraceSampleFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceDateFilter.class.getName())) {
			TraceDateFilter filter = new TraceDateFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventDateFilter.class.getName())) {
			EventDateFilter filter = new EventDateFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventHeadClassifierFilter.class.getName())) {
			EventHeadClassifierFilter filter = new EventHeadClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventTailClassifierFilter.class.getName())) {
			EventTailClassifierFilter filter = new EventTailClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventHeadAttributeFilter.class.getName())) {
			EventHeadAttributeFilter filter = new EventHeadAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventTailAttributeFilter.class.getName())) {
			EventTailAttributeFilter filter = new EventTailAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceDirectlyFollowsClassifierFilter.class.getName())) {
			TraceDirectlyFollowsClassifierFilter filter = new TraceDirectlyFollowsClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceDirectlyFollowsGlobalAttributeFilter.class.getName())) {
			TraceDirectlyFollowsGlobalAttributeFilter filter = new TraceDirectlyFollowsGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceDirectlyFollowsAttributeFilter.class.getName())) {
			TraceDirectlyFollowsAttributeFilter filter = new TraceDirectlyFollowsAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		
		if (name.equals(TraceVariantFirstClassifierFilter.class.getName())) {
			TraceVariantFirstClassifierFilter filter = new TraceVariantFirstClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceVariantFirstGlobalAttributeFilter.class.getName())) {
			TraceVariantFirstGlobalAttributeFilter filter = new TraceVariantFirstGlobalAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(TraceVariantFirstAttributeFilter.class.getName())) {
			TraceVariantFirstAttributeFilter filter = new TraceVariantFirstAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}

		return null;
	}

	public void exportToDocument(Document document, Element cellElement) {
		Element filterElement = document.createElement("filter");
		cellElement.appendChild(filterElement);
		Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(getName()));
		filterElement.appendChild(nameElement);
		parameters.exportToDocument(document, filterElement);
	}

	public void importFromDocument(Document document, Element filterElement) {
		NodeList nodes = filterElement.getElementsByTagName("name");
		if (nodes.getLength() >= 1) {
			setName(nodes.item(0).getTextContent());
		}
		parameters = new ParametersTemplate();
		parameters.importFromDocument(document, filterElement);
	}
}
