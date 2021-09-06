package org.processmining.filterbook.cells;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.deckfour.uitopia.api.model.ViewType;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.filterbook.types.LogType;
import org.processmining.framework.plugin.ProMCanceller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComputationCellTemplate extends CellTemplate {

	/*
	 * Selected view.
	 */
	private String view;
	/*
	 * Selected input log.
	 */
	private String input;
	/*
	 * A filter template for every filter.
	 */
	private List<FilterTemplate> filterTemplates;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public List<FilterTemplate> getFilterTemplates() {
		return filterTemplates;
	}

	public void setFilterTemplates(List<FilterTemplate> filterTemplates) {
		this.filterTemplates = filterTemplates;
	}

	/**
	 * Visualizes the template as HTML.
	 */
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buf = new StringBuffer();
		if (includeHTMLTags) {
			buf.append("<html>");
		}
		buf.append("<li>Computation cell<ol>");
		buf.append("<li>Name: " + StringEscapeUtils.escapeHtml4(getName()) + "</li>");
		buf.append("<li>View: " + StringEscapeUtils.escapeHtml4(view) + "</li>");
		buf.append("<li>Input: " + StringEscapeUtils.escapeHtml4(input) + "</li>");
		buf.append("<li>Filters:<ol>");
		for (FilterTemplate filterTemplate : filterTemplates) {
			buf.append(filterTemplate.toHTMLString(false));
		}
		buf.append("</ol></li>");
		buf.append("</ol></li>");
		if (includeHTMLTags) {
			buf.append("</html>");
		}
		return buf.toString();
	}

	/**
	 * Creates a computation cell from the template.
	 */
	public Cell createCell(UIPluginContext context, ProMCanceller canceller, Notebook notebook, XLog log) {
		//  Construct a new computation cell.
		ComputationCell cell = new ComputationCell(context, canceller, notebook, getName(), notebook.getInputLog(),
				notebook.getOutputLogs());
		// Initialize the widget.
		cell.getWidget(true);
		// Select the proper view.
		for (ViewType viewType : cell.getViews()) {
			if (viewType.getTypeName().equals(view)) {
				cell.setLastView(viewType);
			}
		}
		List<LogType> outputLogs = notebook.getOutputLogs();
		cell.setInputLogs(outputLogs);
		// Select the proper inout log.
		for (LogType logType : outputLogs) {
			if (logType.getName().equals(input)) {
				cell.setInputLog(logType);
			}
		}
		// Add filters. Provide every filter its proper filtered log as input.
		XLog filteredLog = log;
		for (FilterTemplate filterTemplate : filterTemplates) {
			Filter filter = filterTemplate.createFilter(filteredLog, cell);
			cell.add(filter);
			filteredLog = filter.filter();
		}
		cell.updateOutputLog(false);
		return cell;
	}

	/**
	 * Exports template to XML document.
	 */
	public void exportToDocument(Document document, Element notebookElement) {
		Element cellElement = document.createElement("computationCell");
		notebookElement.appendChild(cellElement);
		Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(getName()));
		cellElement.appendChild(nameElement);
		Element viewElement = document.createElement("view");
		viewElement.appendChild(document.createTextNode(getView()));
		cellElement.appendChild(viewElement);
		Element inputElement = document.createElement("input");
		inputElement.appendChild(document.createTextNode(getInput()));
		cellElement.appendChild(inputElement);
		for (FilterTemplate filterTemplate : filterTemplates) {
			filterTemplate.exportToDocument(document, cellElement);
		}
	}

	/**
	 * Imports template from XML document.
	 */
	public void importFromDocument(Document document, Element computationCellElement) {
		NodeList nodes = computationCellElement.getElementsByTagName("name");
		if (nodes.getLength() >= 1) {
			setName(nodes.item(0).getTextContent());
		}
		nodes = computationCellElement.getElementsByTagName("view");
		if (nodes.getLength() >= 1) {
			setView(nodes.item(0).getTextContent());
		}
		nodes = computationCellElement.getElementsByTagName("input");
		if (nodes.getLength() >= 1) {
			setInput(nodes.item(0).getTextContent());
		}
		nodes = computationCellElement.getElementsByTagName("filter");
		filterTemplates = new ArrayList<FilterTemplate>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				FilterTemplate filterTemplate = new FilterTemplate();
				filterTemplate.importFromDocument(document, element);
				filterTemplates.add(filterTemplate);
			}
		}
	}
}
