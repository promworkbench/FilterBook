package org.processmining.filterbook.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;
import org.processmining.filterbook.types.SelectionType;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceLogFilter extends Filter {

	public static final String NAME = "Select on log";

	private XLog cachedLog;
	private SelectionType cachedSelectionType;
	private XLog cachedFilteredLog;
	
	public TraceLogFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	public TraceLogFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		cachedLog = null;
	}

	private boolean hasMatchingUniqueConceptNames() {
		Set<String> conceptNames = new HashSet<String>();
		for (XTrace trace : getCell().getInputLog().getLog()) {
			String conceptName = XConceptExtension.instance().extractName(trace);
			if (conceptNames.contains(conceptName)) {
				return false;
			}
			conceptNames.add(conceptName);
		}
		for (XTrace trace : getLog()) {
			String conceptName = XConceptExtension.instance().extractName(trace);
			if (!(conceptNames.contains(conceptName))) {
				return false;
			}
		}
		return true;
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		if (getCell().getInputLog().getLog() == null) {
			return false;
		}
		if (getCell().getInputLog().getLog().isEmpty()) {
			return false;
		}
		return hasConceptExtension(getLog()) && hasConceptExtension(getCell().getInputLog().getLog())
				&& hasGlobalConceptName(getLog().getGlobalEventAttributes())
				&& hasGlobalConceptName(getCell().getInputLog().getLog().getGlobalEventAttributes())
				&& hasMatchingUniqueConceptNames() && hasTraces();
	}

	public XLog filter() {
		/*
		 * Get the relevant parameters.
		 */
		SelectionType selectionType = getParameters().getOneFromListSelection().getSelected();
		/*
		 * Check whether the cache is  valid.
		 */
		if (cachedLog == getLog()) {
			if (cachedSelectionType == selectionType) { 
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
		Set<String> conceptNames = new HashSet<String>();
		for (XTrace trace : getLog()) {
			conceptNames.add(XConceptExtension.instance().extractName(trace));
		}
		for (XTrace trace : getCell().getInputLog().getLog()) {
			String conceptName = XConceptExtension.instance().extractName(trace);
			boolean match = conceptNames.contains(conceptName);
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
		cachedSelectionType = selectionType;
		cachedFilteredLog = filteredLog;
		return filteredLog;
	}

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL },
				{ 80, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getOneFromListSelection().getWidget(), "0, 0, 1, 0");
		setWidget(widget);
	}

	public void updated(Parameter parameter) {
		getCell().updated();
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
		setSelectionType(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setSelection(getParameters().getOneFromListSelection().getSelected().name());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
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
