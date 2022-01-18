package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.charts.LengthChart;
import org.processmining.filterbook.parameters.MultipleFromListParameter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.parameters.YesNoParameter;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceLengthFilter extends Filter {

	/**
	 * The name of this filter.
	 */
	public static final String NAME = "Select on length";

	private JComponent traceLengthWidget;
	private JComponent roundUpWidget;

	private XLog cachedLog;
	private Set<Integer> cachedSelectedLengths;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;
	
	public TraceLengthFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceLengthFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	/**
	 * This filter is suitable if the log contains at least one trace.
	 */
	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return hasTraces();
	}

	/**
	 * Filter the set log on the start events using the set parameters.
	 */
	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		Set<Integer> selectedLengths = new HashSet<Integer>(getParameters().getMultipleFromListInteger().getSelected());
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedSelectedLengths.equals(selectedLengths) &&
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
		for (XTrace trace : getLog()) {
			boolean match = selectedLengths.contains(trace.size());
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
		cachedSelectedLengths = selectedLengths;
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	/**
	 * Construct a widget for changing the required parameters.
	 */
	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL },
				{ 30, 30, TableLayoutConstants.FILL, 80 } };
		widget.setLayout(new TableLayout(size));
		traceLengthWidget = getParameters().getMultipleFromListInteger().getWidget();
		widget.add(traceLengthWidget, "0, 2, 0, 2");
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 3");
		widget.add(getParameters().getYesNoA().getWidget(), "0, 0");
		roundUpWidget = getParameters().getYesNoB().getWidget();
		//		widget.add(getParameters().getRoundUp().getWidget(), "0, 1");
		
		widget.add(getChartWidget(), "1, 0, 1, 3");

		setWidget(widget);
	}

	private JComponent getChartWidget() {
	    return LengthChart.getChart(getLog());
	}
	
	private void updatedDoInBackground() {
		/*
		 * Reset the trace lengths parameter.
		 */
		setTraceLengths(true);
	}
	
	private void updatedDone() {
		/*
		 * Get the new widget for the trace lengths parameter, and replace the old one
		 * with it.
		 */
		getWidget().remove(traceLengthWidget);
		traceLengthWidget = getParameters().getMultipleFromListInteger().getWidget();
		getWidget().add(traceLengthWidget, "1, 0, 1, 2");
		getWidget().revalidate();
		getWidget().repaint();
		getCell().updated();
	}
	
	/**
	 * Handle if a parameter values was changed.
	 */
	public void updated(Parameter parameter) {
		if (parameter == getParameters().getYesNoA() || parameter == getParameters().getYesNoB()) {
			if (parameter == getParameters().getYesNoA()) {
				setRoundUp(true);
				getWidget().remove(roundUpWidget);
				if (getParameters().getYesNoA().getSelected()) {
					roundUpWidget = getParameters().getYesNoB().getWidget();
					getWidget().add(roundUpWidget, "0, 1");
				}
			}
			getWidget().remove(traceLengthWidget);
			JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			traceLengthWidget = label;
			getWidget().add(traceLengthWidget, "1, 0, 1, 2");
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				public Void doInBackground() {
					updatedDoInBackground();
					return null;
				}

				public void done() {
					updatedDone();
				}
			};
			worker.execute();
			getWidget().revalidate();
			getWidget().repaint();
		}
		getCell().updated();
	}

	private void setTraceLengths(boolean doReset) {
		if (!doReset && getParameters().getMultipleFromListInteger() != null) {
			return;
		}
		Set<Integer> traceLengths = new HashSet<Integer>();
		int maxLength = 0;
		for (XTrace trace : getLog()) {
			traceLengths.add(trace.size());
			maxLength = Math.max(maxLength, trace.size());
		}
		if (getParameters().getYesNoB().getSelected()) {
			if (maxLength < 5) {
				maxLength = 5;
			} else if (maxLength < 10) {
				maxLength = 10;
			} else if (maxLength < 20) {
				maxLength = 20;
			} else if (maxLength < 50) {
				maxLength = 50;
			} else if (maxLength < 100) {
				maxLength = 100;
			} else if (maxLength < 200) {
				maxLength = 200;
			} else if (maxLength < 500) {
				maxLength = 500;
			} else if (maxLength < 1000) {
				maxLength = 1000;
			}
		}
		if (getParameters().getYesNoA().getSelected()) {
			for (int i = 0; i <= maxLength; i++) {
				traceLengths.add(i);
			}
		}
		List<Integer> unsortedLengths = new ArrayList<Integer>(traceLengths);
		List<Integer> selectedLengths = new ArrayList<Integer>(traceLengths);
		if (getParameters().getMultipleFromListInteger() != null) {
			selectedLengths.retainAll(getParameters().getMultipleFromListInteger().getSelected());
		}
		getParameters().setMultipleFromListInteger(new MultipleFromListParameter<Integer>("Select trace lengths", this,
				selectedLengths, unsortedLengths, true));
	}

	/*
	 * Make sure the selection type parameter is initialized.
	 */
	private void setSelectionType(boolean doReset) {
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

	private void setFillGaps(boolean doReset) {
		if (!doReset && getParameters().getYesNoA() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoA() != null) {
			selected = getParameters().getYesNoA().getSelected();
		}
		getParameters().setYesNoA(new YesNoParameter("Add all lengths up to maximal length?", this, selected));
	}

	private void setRoundUp(boolean doReset) {
		if (!doReset && getParameters().getYesNoB() != null) {
			return;
		}
		boolean selected = false;
		if (getParameters().getYesNoB() != null) {
			selected = getParameters().getYesNoB().getSelected();
		}
		getParameters().setYesNoB(new YesNoParameter("Round up maximal length?", this, selected));
	}

	/**
	 * Update the filter parameters.
	 */
	public void updateParameters() {
		setFillGaps(true);
		setRoundUp(true);
		setTraceLengths(true);
		setSelectionType(true);
	}
	
	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setYesNoA(getParameters().getYesNoA().getSelected());
		filterTemplate.getParameters().setYesNoB(getParameters().getYesNoB().getSelected());
		filterTemplate.getParameters().setValuesA(new TreeSet<String>());
		for (Integer selected : getParameters().getMultipleFromListInteger().getSelected()) {
			filterTemplate.getParameters().getValuesA().add(selected.toString());
		}
		filterTemplate.getParameters().setSelection(getParameters().getOneFromListSelection().getSelected().name());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setFillGaps(true);
		getParameters().getYesNoA().setSelected(parameters.isYesNoA());
		setRoundUp(true);
		getParameters().getYesNoB().setSelected(parameters.isYesNoB());
		setTraceLengths(true);
		if (parameters.getValuesA() != null) {
			List<Integer> values = new ArrayList<Integer>();
			for (Integer value : getParameters().getMultipleFromListInteger().getOptions()) {
				if (value != null && parameters.getValuesA().contains(value.toString())) {
					values.add(value);
				}
			}
			getParameters().getMultipleFromListInteger().setSelected(values);
		}
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


