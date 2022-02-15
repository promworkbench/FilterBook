package org.processmining.filterbook.filters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.DateChart;
import org.processmining.filterbook.parameters.DateParameter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.parameters.YesNoParameter;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceDateFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select traces based on date";
	
	private JComponent chartWidget;

	private Date firstLogDate = null;
	private Date lastLogDate = null;

	private XLog cachedLog;
	private Date cachedFirstDate;
	private Date cachedLastDate;
	private boolean cachedYesNoA;
	private boolean cachedYesNoB;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;
	
	TraceDateFilter(XLog log, Parameters parameters, ComputationCell cell) {
		this(NAME, log, parameters, cell);
	}

	TraceDateFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		for (XTrace trace : getLog()) {
			for (XEvent event : trace) {
				Date date = XTimeExtension.instance().extractTimestamp(event);
				if (date == null) {
					continue;
				}
				if (firstLogDate == null || firstLogDate.after(date)) {
					firstLogDate = date;
				}
				if (lastLogDate == null || lastLogDate.before(date)) {
					lastLogDate = date;
				}
			}
		}
		cachedLog = null;
	}

	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		Date firstDate = getParameters().getDateA().getDate();
		Date lastDate = getParameters().getDateB().getDate();
		boolean yesNoA = getParameters().getYesNoA().getSelected();
		boolean yesNoB = getParameters().getYesNoA().getSelected();
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedFirstDate.equals(firstDate) &&
					cachedLastDate.equals(lastDate) &&
					cachedYesNoA == yesNoA &&
					cachedYesNoB == yesNoB &&
					cachedSelectionType == selectionType) {
				/*
				 * Yes, it is. Return the cached filtered log.
				 */
				System.out.println("[" + NAME + "]: Returning cached filtered log.");
				return cachedFilteredLog;
			}
		}
		/*
		 * No, it is not. Filter the log using the relevant parameters.
		 */
		System.out.println("[" + NAME + "]: Returning newly filtered log.");
		XLog filteredLog = initializeLog(getLog());
		Calendar firstCalendar = Calendar.getInstance();
		firstCalendar.setTime(firstDate);
		firstCalendar.set(Calendar.HOUR_OF_DAY, 0);
		firstCalendar.set(Calendar.MINUTE, 0);
		firstCalendar.set(Calendar.SECOND, 0);
		firstCalendar.set(Calendar.MILLISECOND, 0);
		firstDate = firstCalendar.getTime();
		Calendar lastCalendar = Calendar.getInstance();
		lastCalendar.setTime(lastDate);
		lastCalendar.set(Calendar.HOUR_OF_DAY, 23);
		lastCalendar.set(Calendar.MINUTE, 59);
		lastCalendar.set(Calendar.SECOND, 59);
		lastCalendar.set(Calendar.MILLISECOND, 999);
		lastDate = lastCalendar.getTime();
		for (XTrace trace : getLog()) {
			Date firstTraceDate = null;
			Date lastTraceDate = null;
			for (XEvent event : trace) {
				Date date = XTimeExtension.instance().extractTimestamp(event);
				if (date == null) {
					continue;
				}
				if (firstTraceDate == null || firstTraceDate.after(date)) {
					firstTraceDate = date;
				}
				if (lastTraceDate == null || lastTraceDate.before(date)) {
					lastTraceDate = date;
				}
			}
			if (firstTraceDate == null || lastTraceDate == null) {
				continue;
			}
			boolean match = !firstTraceDate.after(lastDate) && !lastTraceDate.before(firstDate);
			if (!yesNoA && !firstTraceDate.after(firstDate)) {
				match = false;
			}
			if (!yesNoB && !lastTraceDate.before(lastDate)) {
				match = false;
			}
			switch (selectionType) {
				case FILTERIN : {
					if (match) {
						filteredLog.add(trace);
					}
					break;
				}
				case FILTEROUT : {
					if (!match) {
						filteredLog.add(trace);
					}
					break;
				}
			}
		}
		/*
		 * Update the cache and return the result.
		 */
		cachedLog = getLog();
		cachedFirstDate = firstDate;
		cachedLastDate = lastDate;
		cachedYesNoA = yesNoA;
		cachedYesNoB = yesNoB;
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL },
				{ TableLayoutConstants.FILL, 30, TableLayoutConstants.FILL, 30, 90 } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getDateA().getWidget(), "0, 0");
		widget.add(getParameters().getDateB().getWidget(), "0, 2");
		widget.add(getParameters().getYesNoA().getWidget(), "0, 1");
		widget.add(getParameters().getYesNoB().getWidget(), "0, 3");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 4");
		chartWidget = getChartWidget();
		widget.add(chartWidget, "1, 0, 1, 4");
		setWidget(widget);
	}

	protected JComponent getChartWidget() {
		return DateChart.getChart(getLog(), firstLogDate, lastLogDate);
	}

	public void updated(Parameter parameter) {
		getCell().updated();
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTimeExtension() && hasGlobalTimestamp() && hasEvents();
	}

	private void setMayStartEarly(boolean doReset) {
		if (!doReset && getParameters().getYesNoA() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoA() != null) {
			selected = getParameters().getYesNoA().getSelected();
		}
		getParameters().setYesNoA(new YesNoParameter("May a trace start before the first date?", this, selected));
	}
	
	private void setMayEndLate(boolean doReset) {
		if (!doReset && getParameters().getYesNoB() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoB() != null) {
			selected = getParameters().getYesNoB().getSelected();
		}
		getParameters().setYesNoB(new YesNoParameter("May a trace end after the last date?", this, selected));
	}
	
	private void setFirstDate(boolean doReset) {
		if (!doReset && getParameters().getDateA() != null) {
			return;
		}
		Date date = firstLogDate;
		if (getParameters().getDateA() != null) {
			date = getParameters().getDateA().getDate();
		}
		getParameters().setDateA(new DateParameter("Select a first date", this, date));
	}
	
	private void setLastDate(boolean doReset) {
		if (!doReset && getParameters().getDateB() != null) {
			return;
		}
		Date date = lastLogDate;
		if (getParameters().getDateB() != null) {
			date = getParameters().getDateB().getDate();
		}
		getParameters().setDateB(new DateParameter("Select a last date", this, date));
	}
	
	void setSelectionType(boolean doReset) {
		if (!doReset && getParameters().getOneFromListSelection() != null) {
			return;
		}
		SelectionType selected = SelectionType.FILTERIN;
		if (getParameters().getOneFromListSelection() != null) {
			selected = getParameters().getOneFromListSelection().getSelected();
		}
		getParameters().setOneFromListSelection(new OneFromListParameter<SelectionType>("Select a selection type", this,
				selected, Arrays.asList(SelectionType.values()), false));
	}
	
	public void updateParameters() {
		setMayStartEarly(true);
		setMayEndLate(true);
		setFirstDate(true);
		setLastDate(true);
		setSelectionType(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setYesNoA(getParameters().getYesNoA().getSelected());
		filterTemplate.getParameters().setYesNoB(getParameters().getYesNoB().getSelected());
		filterTemplate.getParameters().setDateA(getParameters().getDateA().getDate());
		filterTemplate.getParameters().setDateB(getParameters().getDateB().getDate());
		filterTemplate.getParameters().setSelection(getParameters().getOneFromListSelection().getSelected().name());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setMayStartEarly(true);
		getParameters().getYesNoA().setSelected(parameters.isYesNoA());
		setMayEndLate(true);
		getParameters().getYesNoB().setSelected(parameters.isYesNoB());
		setFirstDate(true);
		getParameters().getDateA().setDate(parameters.getDateA());
		setLastDate(true);
		getParameters().getDateB().setDate(parameters.getDateB());
		setSelectionType(true);
		if (parameters.getSelection() != null) {
			for (SelectionType selection : getParameters().getOneFromListSelection().getOptions()) {
				if (parameters.getSelection().equals(selection.name())) {
					getParameters().getOneFromListSelection().setSelected(selection);
				}
			}
		}
	}

}
