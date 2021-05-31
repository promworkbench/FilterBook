package org.processmining.filterbook.notebook;

import java.util.ArrayList;
import java.util.List;

import org.processmining.filterbook.cells.CellTemplate;
import org.processmining.filterbook.cells.ComputationCellTemplate;
import org.processmining.filterbook.cells.TextCellTemplate;
import org.processmining.framework.util.HTMLToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NotebookTemplate implements HTMLToString {

	/*
	 * List of cell templates.
	 */
	private List<CellTemplate> cellTemplates;

	public List<CellTemplate> getCellTemplates() {
		return cellTemplates;
	}

	public void setCellTemplates(List<CellTemplate> cellTemplates) {
		this.cellTemplates = cellTemplates;
	}

	/**
	 * Visualizes the notebook template as HTML.
	 */
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buf = new StringBuffer();
		if (includeHTMLTags) {
			buf.append("<html>");
		}
		buf.append("<ol>");
		buf.append("<li>Notebook<ol>");
		for (CellTemplate cellTemplate : cellTemplates) {
			buf.append(cellTemplate.toHTMLString(false));
		}
		buf.append("</ol></li></ol>");
		if (includeHTMLTags) {
			buf.append("</html>");
		}
		return buf.toString();
	}
	
	/**
	 * Exports the notebook template to the given XML document.
	 * @param document The given XML document.
	 */
	public void exportToDocument(Document document) {
		// Create a root element
		Element notebookElement = document.createElement("notebook");
		document.appendChild(notebookElement);
		// Export all cells as children of the root element.
		for (CellTemplate cellTemplate : cellTemplates) {
			cellTemplate.exportToDocument(document, notebookElement);
		}
	}
	
	/**
	 * Imports the notebook template from the given XML document.
	 * @param document The given XML document.
	 */
	public void importFromDocument(Document document) {
		// Get the root element.
		Element notebookElement = document.getDocumentElement();
		cellTemplates = new ArrayList<CellTemplate>();
		NodeList nodes = notebookElement.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getNodeName().equals("textCell")) {
					// Found a text cell. Import it.
					CellTemplate cellTemplate = new TextCellTemplate();
					cellTemplate.importFromDocument(document, element);
					cellTemplates.add(cellTemplate);
				} else if (element.getNodeName().equals("computationCell")) {
					// Found a computation cell. Import it.
					CellTemplate cellTemplate = new ComputationCellTemplate();
					cellTemplate.importFromDocument(document, element);
					cellTemplates.add(cellTemplate);
				}
			}
		}
	}
}
