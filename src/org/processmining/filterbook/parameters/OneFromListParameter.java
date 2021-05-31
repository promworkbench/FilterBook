package org.processmining.filterbook.parameters;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.filterbook.filters.Filter;
import org.processmining.framework.util.ui.widgets.ProMList;

public class OneFromListParameter<T extends Comparable<T>> extends Parameter implements ListSelectionListener {

	/*
	 * The selected item.
	 */
	private T selected;
	/*
	 * The list of items.
	 */
	private List<T> options;
	/*
	 * The sorted list of items. If null, then the itmes need not be sorted.
	 */
	private List<T> sortedOptions;
	
	/*
	 * The widget showing the (sorted) list of items.
	 */
	private ProMList<T> list;

	/*
	 * Empty constructor. Used for import/export.
	 */
	public OneFromListParameter() {
	}

	public OneFromListParameter(String label, Filter filter, T selected, List<T> options, boolean useSortedOptions) {
		super(label, filter);
		this.options = new ArrayList<T>(options);
		if (useSortedOptions) {
			this.sortedOptions = new ArrayList<T>(options);
			Collections.sort(this.options);
		} else {
			this.sortedOptions = null;
		}
		this.selected = selected;
		this.list = null;
	}

	public T getSelected() {
		return selected;
	}

	public void setSelected(T selected) {
		this.selected = selected;
		if (selected instanceof Filter) {
			getFilter().setSelected((Filter) selected);
		}
	}

	public List<T> getOptions() {
		return sortedOptions != null ? sortedOptions : options;
	}

	public void setOptions(List<T> options) {
		this.options = new ArrayList<T>(options);
		if (this.sortedOptions != null) {
			this.sortedOptions = new ArrayList<T>(options);
			Collections.sort(this.options);
		}
	}

	public boolean doUseSortedOptions() {
		return sortedOptions != null;
	}

	public void setUseSortedOptions(boolean useSortedOptions) {
		if (useSortedOptions) {
			this.sortedOptions = new ArrayList<T>(options);
			Collections.sort(this.options);
		} else {
			sortedOptions = null;
		}
	}
	
	public JComponent getWidget() {
		DefaultListModel<T> listModel = new DefaultListModel<T>();
		for (T option: options) {
			listModel.addElement(option);
		}
		list = new ProMList<T>(getLabel(), listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (selected != null) {
			list.setSelection(selected);
		} else {
			list.setSelection(new ArrayList<T>());
		}
		list.addListSelectionListener(this);
		list.setPreferredSize(new Dimension(100, 100));
		return list;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		List<T> selected = list.getSelectedValuesList();
		if (selected.size() == 1) {
			this.setSelected(selected.get(0));
			// Notify the filter that this parameter has been updated.
			getFilter().updated(this);
		} else {
			list.setSelection(selected);
		}
	}
}
