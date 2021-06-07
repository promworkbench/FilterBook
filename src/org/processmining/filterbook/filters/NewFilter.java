package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.deckfour.xes.model.XLog;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;

public class NewFilter extends Filter {

	public static final String NAME = "Add filter...";

	private OneFromListParameter<Filter> filterList;

	public NewFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public XLog filter() {
		return getLog();
	}

	public void constructWidget() {
		// TODO Auto-generated method stub
		List<Filter> filters = new ArrayList<Filter>();
		
		filters.add(new LogGlobalsFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new TraceFirstEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new TraceFirstEventClassifierFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new TraceLastEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new TraceLastEventClassifierFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new TraceGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new TraceAttributeFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new TraceLengthFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new EventClassifierFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventAttributeFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new EventFirstEventAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventFirstEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventFirstEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filters.add(new EventLastEventAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventLastEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventLastEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filters.add(new EventFirstLastEventAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventFirstLastEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventFirstLastEventClassifierFilter(getLog(), new Parameters(), getCell()));
		
		filters.add(new TraceLogFilter(getLog(), new Parameters(), getCell()));
		filters.add(new TraceUniqueNameFilter(getLog(), new Parameters(), getCell()));

		filters.add(new TraceFirstLastEventFilter(getLog(), new Parameters(), getCell()));

		filters.add(new TraceDateFilter(getLog(), new Parameters(), getCell()));
		filters.add(new EventDateFilter(getLog(), new Parameters(), getCell()));

	
		List<Filter> suitableFilters = new ArrayList<Filter>();
		for (Filter filter : filters) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Filter " + filter.getName() + " added.");
				suitableFilters.add(filter);
			}
		}
		filterList = new OneFromListParameter<Filter>("Select a new filter", this, null, suitableFilters, true);
		setWidget(filterList.getWidget());
	}

	public void updatedDoInBackground(Parameter parameter) {
		if (getCell() != null) {
			getCell().add(((OneFromListParameter<Filter>) parameter).getSelected());
		}
	}
	
	public void updatedDone() {
		getCell().getWidget(false).revalidate();
		getCell().getWidget(false).repaint();
	}
	
	public void updated(Parameter parameter) {
		JLabel  label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
		label.setHorizontalAlignment(JLabel.CENTER);
		getCell().setMainWidget(label);
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			public Void doInBackground() {
				updatedDoInBackground(parameter);
				return null;
			}

			public void done() {
				updatedDone();
			}
		};
		worker.execute();
		setWidget(null);
		getCell().getWidget(false).revalidate();
		getCell().getWidget(false).repaint();
	}

	public void updateParameters() {
		
	}

	public boolean isSuitable() {
		return true;
	}
	
	public FilterTemplate getTemplate() {
		return null;
	}

	public void setTemplate(ParametersTemplate parameters) {
	}
}
