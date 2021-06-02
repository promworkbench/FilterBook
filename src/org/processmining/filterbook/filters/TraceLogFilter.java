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

	public TraceLogFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public TraceLogFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
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
		return hasGlobalConceptName(getLog()) && hasGlobalConceptName(getCell().getInputLog().getLog()) && hasMatchingUniqueConceptNames(); 
	}

	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		Set<String> conceptNames = new HashSet<String>();
		for (XTrace trace : getLog()) {
			conceptNames.add(XConceptExtension.instance().extractName(trace));
		}
		for (XTrace trace : getCell().getInputLog().getLog()) {
			String conceptName = XConceptExtension.instance().extractName(trace);
			boolean match = conceptNames.contains(conceptName);
			switch (getParameters().getOneFromListSelection().getSelected()) {
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
