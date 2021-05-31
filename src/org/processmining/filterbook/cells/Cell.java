package org.processmining.filterbook.cells;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.filterbook.notebook.Notebook;

public abstract class Cell {

	/*
	 * The context for this cell. This should be a proper context, as a computation
	 * cell uses this context to run its filters.
	 */
	private final UIPluginContext context;
	/*
	 * The notebook in which this cell resides.
	 */
	private final Notebook notebook;
	/*
	 * The name of this cell.
	 */
	private final String name;

	/**
	 * Construct a cell with a given context, notebook, and name.
	 * @param context The given context
	 * @param notebook The given notebook
	 * @param name The given name
	 */
	public Cell(UIPluginContext context, Notebook notebook, String name) {
		this.context = context;
		this.notebook = notebook;
		this.name = name;
	}

	/**
	 * Abstract method to call if the cell has been updated.
	 */
	public abstract void updated();

	/**
	 * Gets the cell's name.
	 * @return The cell's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the cell's context.
	 * @return The cell's context
	 */
	public UIPluginContext getContext() {
		return context;
	}

	/**
	 * Gets the cell's notebook.
	 * @return The cell's notebook
	 */
	public Notebook getNotebook() {
		return notebook;
	}

	/**
	 * Abstract method to get a widget for this cell.
	 * @param doReset Whether to ignore any old widget.
	 * @return A widget for this cell.
	 */
	public abstract JComponent getWidget(boolean doReset);

	/**
	 * Abstract method to call if the cell needs to be updated.
	 */
	public abstract void update();

	/**
	 * Abstract method to get a cell template for this cell.
	 * @return A cell template for this cell
	 */
	public abstract CellTemplate getTemplate();
}
