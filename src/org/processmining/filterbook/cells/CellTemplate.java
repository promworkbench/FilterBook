package org.processmining.filterbook.cells;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.util.HTMLToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class CellTemplate implements HTMLToString {

	/*
	 * Name of the cell.
	 */
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public abstract Cell createCell(UIPluginContext context, ProMCanceller canceller, Notebook notebook, XLog log);

	public abstract void exportToDocument(Document document, Element element);
	
	public abstract void importFromDocument(Document document, Element element);
}
