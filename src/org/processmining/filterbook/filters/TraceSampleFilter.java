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
import org.processmining.filterbook.parameters.YesNoParameter;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TraceSampleFilter extends Filter {

	public static final String NAME = "Select sample";

	/*
	 * If available, the last input, the last sample size used, and the last output.
	 */
	private XLog inputLog;
	private Integer sampleSize;
	private XLog outputLog;

	public TraceSampleFilter(XLog log, Parameters parameters, ComputationCell cell) {
		this(NAME, log, parameters, cell);
	}

	public TraceSampleFilter(String name, XLog log, Parameters parameters, ComputationCell cell) {
		super(name, parameters, cell);
		setLog(log);
		/*
		 * Nothing avaiable yet.
		 */
		inputLog = null;
		sampleSize = -1;
		outputLog = null;
	}

	public XLog filter() {
		if (!getParameters().getYesNoA().getSelected() || !getLog().equals(inputLog)
				|| !sampleSize.equals(getParameters().getNumberA().getNumber()) || outputLog == null) {
			/*
			 * outputLog is not current. make it current by applying the filter again.
			 */
			inputLog = getLog();
			sampleSize = getParameters().getNumberA().getNumber();
			outputLog = initializeLog(inputLog);
			/*
			 * Copy all traces.
			 */
			outputLog.addAll(inputLog);
			/*
			 * Now continue to remove a trace at random until the sample size is reached.
			 */
			int filteredSize = outputLog.size();
			Random r = new Random();
			while (filteredSize > sampleSize) {
				int t = r.nextInt(filteredSize);
				outputLog.remove(t);
				filteredSize--;
			}
		}
		return outputLog;
	}

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 80, 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(getParameters().getNumberA().getWidget(), "0, 0");
		widget.add(getParameters().getYesNoA().getWidget(), "0, 1");
		setWidget(widget);
	}

	public void updated(Parameter parameter) {
		getCell().updated();
	}

	public boolean isSuitable() {
		if (getLog() == null) {
			return false;
		}
		if (getLog().isEmpty()) {
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

	void setUseCache(boolean doReset) {
		if (!doReset && getParameters().getYesNoA() != null) {
			return;
		}
		boolean selected = true;
		if (getParameters().getYesNoA() != null) {
			selected = getParameters().getYesNoA().getSelected();
		}
		getParameters().setYesNoA(new YesNoParameter("Use previous sample if still valid?", this, selected));
	}

	public void updateParameters() {
		setSampleSize(true);
		setUseCache(true);
	}

	public FilterTemplate getTemplate() {
		FilterTemplate filterTemplate = new FilterTemplate();
		filterTemplate.setName(getClass().getName());
		filterTemplate.setParameters(new ParametersTemplate());
		filterTemplate.getParameters().setYesNoA(getParameters().getYesNoA().getSelected());
		filterTemplate.getParameters().setNumberA(getParameters().getNumberA().getNumber());
		return filterTemplate;
	}

	public void setTemplate(ParametersTemplate parameters) {
		setSampleSize(true);
		getParameters().getNumberA().setNumber(parameters.getNumberA());
		setUseCache(true);
		getParameters().getYesNoA().setSelected(parameters.isYesNoA());
	}

}
