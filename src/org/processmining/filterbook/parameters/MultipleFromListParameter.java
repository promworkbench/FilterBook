package org.processmining.filterbook.parameters;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.filterbook.filters.Filter;
import org.processmining.framework.util.ui.widgets.ProMList;

public class MultipleFromListParameter <T extends Comparable<T>> extends Parameter implements ListSelectionListener {

	/*
	 * The list of selected items.
	 */
	private List<T> selected;
	private Set<T> selectedSet;
	/*
	 * The list of items.
	 */
	private List<T> options;
	/*
	 * The sorted list of items. If null then the item list need not be sorted.
	 */
	private List<T> sortedOptions;

	/*
	 * The widget showing the (sorted) list of items.
	 */
	private ProMList<T> list;

	/*
	 * Empty constructor. Used for import/export.
	 */
	public MultipleFromListParameter() {
	}

	public MultipleFromListParameter(String label, Filter filter, List<T> selected, List<T> options, boolean useSortedOptions) {
		super(label, filter);
		this.options = new ArrayList<T>(options);
		if (useSortedOptions) {
			this.sortedOptions = new ArrayList<T>(options);
			Collections.sort(this.options);
		} else {
			this.sortedOptions = null;
		}
		this.selected = new ArrayList<T>(selected);
		this.selectedSet = new HashSet<T>(selected);
		this.list = null;
	}

	public List<T> getSelected() {
		return selected;
	}

	public void setSelected(List<T> selected) {
		this.selected = new ArrayList<T>(selected);
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
		/*
		 * list.setSelection() seems not to work, but list.setSelectedIndices() does.
		 * Create array containing selected indices.
		 */
		int selectedIndices[] = new int[this.selected.size()];
		int optionsIndex = 0; // index for options
		int selectedIndex = 0; // index for selected options
		for (T option: options) {
			listModel.addElement(option);
			if (this.selectedSet.contains(option)) {
				selectedIndices[selectedIndex++] = optionsIndex;
			}
			optionsIndex++;
		}
		list = new ProMList<T>(getLabel(), listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		if (options.size() == selectedIndex) {
			/*
			 * Faster way to select all items in the list.
			 */
			list.getList().setSelectionInterval(0, options.size() - 1);
		} else {
			/*
			 * Slower way to select some items in the list.
			 */
			list.setSelectedIndices(selectedIndices);
		}
		list.addListSelectionListener(this);
		list.setPreferredSize(new Dimension(100, 100));
		return list;
	}

	public void valueChanged(ListSelectionEvent e) {
		selected.clear();
		selected.addAll(list.getSelectedValuesList());
		// Notify the filter that this parameter has been updated.
		getFilter().updated(this);
	}

}
