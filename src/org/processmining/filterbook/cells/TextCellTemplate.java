package org.processmining.filterbook.cells;

import org.apache.commons.lang3.StringEscapeUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.framework.plugin.ProMCanceller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TextCellTemplate extends CellTemplate {

	/*
	 * The informative text 
	 */
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Visualizes the template as HTML.
	 */
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buf = new StringBuffer();
		if (includeHTMLTags) {
			buf.append("<html>");
		}
		buf.append("<li>Text cell<ol>");
		buf.append("<li>Name: " + StringEscapeUtils.escapeHtml4(getName()) + "</li>");
		buf.append("<li>Text: " + StringEscapeUtils.escapeHtml4(getText()) + "</li>");
		buf.append("</ol></li>");
		if (includeHTMLTags) {
			buf.append("</html>");
		}
		return buf.toString();
	}

	/**
	 * Creates a cell from this template.
	 */
	public Cell createCell(UIPluginContext context, ProMCanceller canceller, Notebook notebook, XLog log) {
		TextCell cell = new TextCell(context, notebook, getName());
		cell.getWidget(true);
		cell.setText(getText());
		return cell;
	}

	/**
	 * Exports template to XML document.
	 */
	public void exportToDocument(Document document, Element notebookElement) {
		Element cellElement = document.createElement("textCell");
		notebookElement.appendChild(cellElement);
		Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(getName()));
		cellElement.appendChild(nameElement);
		Element textElement = document.createElement("text");
		textElement.appendChild(document.createTextNode(getText()));
		cellElement.appendChild(textElement);
	}

	/**
	 * Imports template from XML document.
	 */
	public void importFromDocument(Document document, Element textCellElement) {
		NodeList nodes = textCellElement.getElementsByTagName("name");
		if (nodes.getLength() >= 1) {
			setName(nodes.item(0).getTextContent());
		}
		nodes = textCellElement.getElementsByTagName("text");
		if (nodes.getLength() >= 1) {
			setText(nodes.item(0).getTextContent());
		}
	}
}
