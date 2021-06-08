package org.processmining.filterbook.filters;

import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.model.XLog;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.NumberParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceSampleFilter extends Filter {

	public static final String NAME = "Select sample";

	public TraceSampleFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public TraceSampleFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
	}

	public XLog filter() {
		XLog filteredLog = initializeLog(getLog());
		filteredLog.addAll(getLog());
		int filteredSize = filteredLog.size();
		Random r = new Random();
		while (filteredSize > getParameters().getNumberA().getNumber()) {
			int t = r.nextInt(filteredSize);
			filteredLog.remove(t);
			filteredSize--;
		}
		return filteredLog;
	}

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 80, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getNumberA().getWidget(), "0, 0");
		setWidget(widget);
	}

	public void updated(Parameter parameter) {
		getCell().updated();
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		return true;
	}

	void setSampleSize(boolean doReset) {
		if (!doReset && getParameters().getOneFromListSelection() != null) {
			return;
		}
		int number = getLog().size() / 2;
		if (getParameters().getNumberA() != null) {
			number = getParameters().getNumberA().getNumber();
		}
		getParameters().setNumberA(new NumberParameter("Select a sample size", this, number, 0, getLog().size()));
	}

	public void updateParameters() {
		setSampleSize(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setNumberA(getParameters().getNumberA().getNumber());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setSampleSize(true);
		getParameters().getNumberA().setNumber(parameters.getNumberA());
	}

}
