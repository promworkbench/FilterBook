package org.processmining.filterbook.parameters;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.processmining.filterbook.filters.Filter;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class YesNoParameter extends Parameter implements ActionListener {

	/*
	 * Whether "yes".
	 */
	private boolean selected;

	/*
	 * Widget for the option.
	 */
	private JCheckBox checkBox;

	/*
	 * Empty constructor. Used for import/export.
	 */
	public YesNoParameter() {
	}

	public YesNoParameter(String label, Filter filter, boolean selected) {
		super(label, filter);
		this.selected = selected;
		checkBox = null;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public JComponent getWidget() {
		checkBox = SlickerFactory.instance().createCheckBox(getLabel(), false);
		checkBox.setSelected(selected);
		checkBox.addActionListener(this);
		checkBox.setOpaque(false);
		checkBox.setPreferredSize(new Dimension(100, 100));
		return checkBox;
	}

	public void actionPerformed(ActionEvent e) {
		setSelected(checkBox.isSelected());
		// Notify the filter that this parameters has been updated.
		getFilter().updated(this);
	}
}
