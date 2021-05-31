package org.processmining.filterbook.parameters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.filterbook.filters.Filter;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.toedter.calendar.JCalendar;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class DateParameter extends Parameter implements PropertyChangeListener {

	private Date date;
	private JCalendar calendar;

	public DateParameter() {

	}

	public DateParameter(String label, Filter filter, Date date) {
		super(label, filter);
		this.date = date;
	}

	public JComponent getWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		calendar = new JCalendar();
		calendar.getDayChooser().setBackground(WidgetColors.COLOR_LIST_SELECTION_FG);
		calendar.getDayChooser().setDecorationBackgroundColor(WidgetColors.COLOR_LIST_SELECTION_FG);
		calendar.getDayChooser().getDayPanel().setBackground(WidgetColors.COLOR_LIST_SELECTION_FG);
		calendar.setDate(date);
		calendar.addPropertyChangeListener(this);
		widget.add(new JLabel(getLabel()), "0, 0");
		widget.add(calendar, "0, 1");
		return widget;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		if (!date.equals(calendar.getDate())) {
			setDate(calendar.getDate());
			getFilter().updated(this);
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
