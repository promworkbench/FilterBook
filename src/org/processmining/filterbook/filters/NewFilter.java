package org.processmining.filterbook.filters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.model.XLog;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.filters.misc.LogGlobalsFilter;
import org.processmining.filterbook.filters.misc.TraceFirstLastEventFilter;
import org.processmining.filterbook.filters.misc.TraceLastAttributeFilter;
import org.processmining.filterbook.filters.misc.TraceLogFilter;
import org.processmining.filterbook.filters.misc.TraceUniqueNameFilter;
import org.processmining.filterbook.filters.project.attribute.EventAttributeFilter;
import org.processmining.filterbook.filters.project.attribute.EventFirstEventAttributeFilter;
import org.processmining.filterbook.filters.project.attribute.EventFirstEventAttributeTraceFilter;
import org.processmining.filterbook.filters.project.attribute.EventFirstLastEventAttributeFilter;
import org.processmining.filterbook.filters.project.attribute.EventFirstLastEventAttributeTraceFilter;
import org.processmining.filterbook.filters.project.attribute.EventHeadAttributeFilter;
import org.processmining.filterbook.filters.project.attribute.EventLastEventAttributeFilter;
import org.processmining.filterbook.filters.project.attribute.EventLastEventAttributeTraceFilter;
import org.processmining.filterbook.filters.project.attribute.EventOccurrencesAttributeFilter;
import org.processmining.filterbook.filters.project.attribute.EventTailAttributeFilter;
import org.processmining.filterbook.filters.project.classifier.EventClassifierFilter;
import org.processmining.filterbook.filters.project.classifier.EventFirstEventClassifierFilter;
import org.processmining.filterbook.filters.project.classifier.EventFirstEventClassifierTraceFilter;
import org.processmining.filterbook.filters.project.classifier.EventFirstLastEventClassifierFilter;
import org.processmining.filterbook.filters.project.classifier.EventFirstLastEventClassifierTraceFilter;
import org.processmining.filterbook.filters.project.classifier.EventHeadClassifierFilter;
import org.processmining.filterbook.filters.project.classifier.EventLastEventClassifierFilter;
import org.processmining.filterbook.filters.project.classifier.EventLastEventClassifierTraceFilter;
import org.processmining.filterbook.filters.project.classifier.EventOccurrencesClassifierFilter;
import org.processmining.filterbook.filters.project.classifier.EventTailClassifierFilter;
import org.processmining.filterbook.filters.project.global.EventDateFilter;
import org.processmining.filterbook.filters.project.global.EventFirstEventGlobalAttributeFilter;
import org.processmining.filterbook.filters.project.global.EventFirstEventGlobalAttributeTraceFilter;
import org.processmining.filterbook.filters.project.global.EventFirstLastEventGlobalAttributeFilter;
import org.processmining.filterbook.filters.project.global.EventFirstLastEventGlobalAttributeTraceFilter;
import org.processmining.filterbook.filters.project.global.EventGlobalAttributeFilter;
import org.processmining.filterbook.filters.project.global.EventLastEventGlobalAttributeFilter;
import org.processmining.filterbook.filters.project.global.EventLastEventGlobalAttributeTraceFilter;
import org.processmining.filterbook.filters.project.global.EventOccurrencesGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.TraceLengthFilter;
import org.processmining.filterbook.filters.select.TraceSampleFilter;
import org.processmining.filterbook.filters.select.attribute.TraceAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceDirectlyFollowsAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceOccurrencesAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceVariantDFCoverAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceVariantFastestAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceVariantFirstAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceVariantLastAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceVariantRandomAttributeFilter;
import org.processmining.filterbook.filters.select.attribute.TraceVariantSlowestAttributeFilter;
import org.processmining.filterbook.filters.select.classifier.TraceDirectlyFollowsClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceFirstEventClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceLastEventClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceOccurrencesClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceVariantDFCoverClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceVariantFastestClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceVariantFirstClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceVariantLastClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceVariantRandomClassifierFilter;
import org.processmining.filterbook.filters.select.classifier.TraceVariantSlowestClassifierFilter;
import org.processmining.filterbook.filters.select.global.TraceDateFilter;
import org.processmining.filterbook.filters.select.global.TraceDirectlyFollowsGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceFirstEventGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceLastEventGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceOccurrencesGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceVariantDFCoverGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceVariantFastestGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceVariantFirstGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceVariantLastGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceVariantRandomGlobalAttributeFilter;
import org.processmining.filterbook.filters.select.global.TraceVariantSlowestGlobalAttributeFilter;
import org.processmining.filterbook.parameters.OneFromListParameter;
import org.processmining.filterbook.parameters.Parameter;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.parameters.ParametersTemplate;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class NewFilter extends Filter {

	public static final String NAME = "Add filter...";

	private OneFromListParameter<Filter> filterSelectOnClassifierList;
	private OneFromListParameter<Filter> filterSelectOnGlobalAttributeList;
	private OneFromListParameter<Filter> filterSelectOnAttributeList;
	private OneFromListParameter<Filter> filterSelectMiscList;
	private OneFromListParameter<Filter> filterProjectOnClassifierList;
	private OneFromListParameter<Filter> filterProjectOnGlobalAttributeList;
	private OneFromListParameter<Filter> filterProjectOnAttributeList;
	private OneFromListParameter<Filter> filterProjectMiscList;
	private OneFromListParameter<Filter> filterMetaList;

	public NewFilter(XLog log, Parameters parameters, ComputationCell cell) {
		super(NAME, parameters, cell);
		setLog(log);
	}

	public XLog filter() {
		return getLog();
	}

	public void constructWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL, TableLayoutConstants.FILL }, { TableLayoutConstants.FILL,
				TableLayoutConstants.FILL, TableLayoutConstants.FILL, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		// TODO Auto-generated method stub
		List<Filter> filtersSelectOnClassifier = new ArrayList<Filter>();
		List<Filter> filtersSelectOnGlobalAttribute = new ArrayList<Filter>();
		List<Filter> filtersSelectOnAttribute = new ArrayList<Filter>();
		List<Filter> filtersSelectMisc = new ArrayList<Filter>();
		List<Filter> filtersProjectOnClassifier = new ArrayList<Filter>();
		List<Filter> filtersProjectOnGlobalAttribute = new ArrayList<Filter>();
		List<Filter> filtersProjectOnAttribute = new ArrayList<Filter>();
		List<Filter> filtersProjectMisc = new ArrayList<Filter>();
		List<Filter> filtersMeta = new ArrayList<Filter>();

		filtersMeta.add(new LogGlobalsFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnGlobalAttribute.add(new TraceFirstEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnClassifier.add(new TraceFirstEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnGlobalAttribute.add(new TraceLastEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnClassifier.add(new TraceLastEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnGlobalAttribute.add(new TraceGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceAttributeFilter(getLog(), new Parameters(), getCell()));

		filtersSelectMisc.add(new TraceLengthFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnAttribute.add(new TraceOccurrencesAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceOccurrencesGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnClassifier.add(new TraceOccurrencesClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventOccurrencesAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventOccurrencesGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventOccurrencesClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnClassifier.add(new EventClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnAttribute.add(new EventAttributeFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventFirstEventAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventFirstEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventFirstEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventFirstEventAttributeTraceFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventFirstEventGlobalAttributeTraceFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventFirstEventClassifierTraceFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventLastEventAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventLastEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventLastEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventLastEventAttributeTraceFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventLastEventGlobalAttributeTraceFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventLastEventClassifierTraceFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventFirstLastEventAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventFirstLastEventGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventFirstLastEventClassifierFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnAttribute.add(new EventFirstLastEventAttributeTraceFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventFirstLastEventGlobalAttributeTraceFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventFirstLastEventClassifierTraceFilter(getLog(), new Parameters(), getCell()));

		filtersMeta.add(new TraceLogFilter(getLog(), new Parameters(), getCell()));
		filtersMeta.add(new TraceUniqueNameFilter(getLog(), new Parameters(), getCell()));

		filtersMeta.add(new TraceFirstLastEventFilter(getLog(), new Parameters(), getCell()));
		filtersMeta.add(new TraceLastAttributeFilter(getLog(), new Parameters(), getCell()));
		
		filtersSelectMisc.add(new TraceSampleFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnGlobalAttribute.add(new TraceDateFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnGlobalAttribute.add(new EventDateFilter(getLog(), new Parameters(), getCell()));

		filtersProjectOnClassifier.add(new EventHeadClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnClassifier.add(new EventTailClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnAttribute.add(new EventHeadAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersProjectOnAttribute.add(new EventTailAttributeFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnClassifier.add(new TraceDirectlyFollowsClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceDirectlyFollowsGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceDirectlyFollowsAttributeFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnClassifier.add(new TraceVariantFirstClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceVariantFirstGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceVariantFirstAttributeFilter(getLog(), new Parameters(), getCell()));
		
		filtersSelectOnClassifier.add(new TraceVariantLastClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceVariantLastGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceVariantLastAttributeFilter(getLog(), new Parameters(), getCell()));
		
		filtersSelectOnClassifier.add(new TraceVariantRandomClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceVariantRandomGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceVariantRandomAttributeFilter(getLog(), new Parameters(), getCell()));
		
		filtersSelectOnClassifier.add(new TraceVariantDFCoverClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceVariantDFCoverGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceVariantDFCoverAttributeFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnClassifier.add(new TraceVariantFastestClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceVariantFastestGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceVariantFastestAttributeFilter(getLog(), new Parameters(), getCell()));

		filtersSelectOnClassifier.add(new TraceVariantSlowestClassifierFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnGlobalAttribute.add(new TraceVariantSlowestGlobalAttributeFilter(getLog(), new Parameters(), getCell()));
		filtersSelectOnAttribute.add(new TraceVariantSlowestAttributeFilter(getLog(), new Parameters(), getCell()));

		List<Filter> suitableSelectOnClassifierFilters = new ArrayList<Filter>();
		for (Filter filter : filtersSelectOnClassifier) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Select on Classifier Filter " + filter.getName() + " added.");
				suitableSelectOnClassifierFilters.add(filter);
			}
		}
		List<Filter> suitableSelectOnGlobalAttributeFilters = new ArrayList<Filter>();
		for (Filter filter : filtersSelectOnGlobalAttribute) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Select on Global Attribute Filter " + filter.getName() + " added.");
				suitableSelectOnGlobalAttributeFilters.add(filter);
			}
		}
		List<Filter> suitableSelectOnAttributeFilters = new ArrayList<Filter>();
		for (Filter filter : filtersSelectOnAttribute) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Select on Attribute Filter " + filter.getName() + " added.");
				suitableSelectOnAttributeFilters.add(filter);
			}
		}
		List<Filter> suitableSelectMiscFilters = new ArrayList<Filter>();
		for (Filter filter : filtersSelectMisc) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Select on Miscellaneous Filter " + filter.getName() + " added.");
				suitableSelectMiscFilters.add(filter);
			}
		}
		List<Filter> suitableProjectOnClassifierFilters = new ArrayList<Filter>();
		for (Filter filter : filtersProjectOnClassifier) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Project on Classifier Filter " + filter.getName() + " added.");
				suitableProjectOnClassifierFilters.add(filter);
			}
		}
		List<Filter> suitableProjectOnGlobalAttributeFilters = new ArrayList<Filter>();
		for (Filter filter : filtersProjectOnGlobalAttribute) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Project on Global Attribute Filter " + filter.getName() + " added.");
				suitableProjectOnGlobalAttributeFilters.add(filter);
			}
		}
		List<Filter> suitableProjectOnAttributeFilters = new ArrayList<Filter>();
		for (Filter filter : filtersProjectOnAttribute) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Project on Attribute Filter " + filter.getName() + " added.");
				suitableProjectOnAttributeFilters.add(filter);
			}
		}
		List<Filter> suitableProjectMiscFilters = new ArrayList<Filter>();
		for (Filter filter : filtersProjectMisc) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Project on Miscellaneous Filter " + filter.getName() + " added.");
				suitableProjectMiscFilters.add(filter);
			}
		}
		List<Filter> suitableMetaFilters = new ArrayList<Filter>();
		for (Filter filter : filtersMeta) {
			if (filter.isSuitable()) {
				System.out.println("[NewFilter] Meta Filter " + filter.getName() + " added.");
				suitableMetaFilters.add(filter);
			}
		}
		filterSelectOnClassifierList = new OneFromListParameter<Filter>("Select a select-on-classifier filter", this, null, suitableSelectOnClassifierFilters, true);
		filterSelectOnGlobalAttributeList = new OneFromListParameter<Filter>("Select a select-on-global-attribute filter", this, null, suitableSelectOnGlobalAttributeFilters, true);
		filterSelectOnAttributeList = new OneFromListParameter<Filter>("Select a select-on-attribute filter", this, null, suitableSelectOnAttributeFilters, true);
		filterSelectMiscList = new OneFromListParameter<Filter>("Select a select-on-other filter", this, null, suitableSelectMiscFilters, true);
		filterProjectOnClassifierList = new OneFromListParameter<Filter>("Select a project-on-classifier filter", this, null, suitableProjectOnClassifierFilters, true);
		filterProjectOnGlobalAttributeList = new OneFromListParameter<Filter>("Select a project-on-global-atttribute filter", this, null, suitableProjectOnGlobalAttributeFilters, true);
		filterProjectOnAttributeList = new OneFromListParameter<Filter>("Select a project-on-attribute filter", this, null, suitableProjectOnAttributeFilters, true);
		filterProjectMiscList = new OneFromListParameter<Filter>("Select a project-on-other filter", this, null, suitableProjectMiscFilters, true);
		filterMetaList = new OneFromListParameter<Filter>("Select a meta filter", this, null, suitableMetaFilters, true);
		
		widget.add(filterSelectOnClassifierList.getWidget(), "0, 0");
		widget.add(filterSelectOnGlobalAttributeList.getWidget(), "0, 1");
		widget.add(filterSelectOnAttributeList.getWidget(), "0, 2");
		widget.add(filterSelectMiscList.getWidget(), "0, 3");
		widget.add(filterProjectOnClassifierList.getWidget(), "1, 0");
		widget.add(filterProjectOnGlobalAttributeList.getWidget(), "1, 1");
		widget.add(filterProjectOnAttributeList.getWidget(), "1, 2");
		widget.add(filterProjectMiscList.getWidget(), "1, 3");
		widget.add(filterMetaList.getWidget(), "2, 0, 2, 3");
		
		setWidget(widget);
	}

	@SuppressWarnings("unchecked")
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
		JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
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
