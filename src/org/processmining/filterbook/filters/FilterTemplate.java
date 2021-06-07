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
		if (name.equals(EventFirstEventClassifierFilter.class.getName())) {
			EventFirstEventClassifierFilter filter = new EventFirstEventClassifierFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventAttributeFilter.class.getName())) {
			EventFirstLastEventAttributeFilter filter = new EventFirstLastEventAttributeFilter(log, new Parameters(), cell);
			filter.setTemplate(parameters);
			return filter;
		}
		if (name.equals(EventFirstLastEventClassifierFilter.class.getName())) {
			EventFirstLastEventClassifierFilter filter = new EventFirstLastEventClassifierFilter(log, new Parameters(), cell);
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
		if (name.equals(EventLastEventClassifierFilter.class.getName())) {
			EventLastEventClassifierFilter filter = new EventLastEventClassifierFilter(log, new Parameters(), cell);
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
