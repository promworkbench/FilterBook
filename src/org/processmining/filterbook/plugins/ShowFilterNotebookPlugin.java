package org.processmining.filterbook.plugins;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin( //
		name = "Start Filter Notebook", //
		parameterLabels = { "Event Log", "Canceller", "Notebook" }, //
		returnLabels = { "Filter Notebook" }, //
		returnTypes = { JComponent.class }, //
		level = PluginLevel.PeerReviewed, // Good enough for ProM Lite
		userAccessible = true, //
		//		icon = "prom_duck.png", //
		//		url = "https://www.win.tue.nl/~hverbeek/blog/2019/09/30/visualize-log-as-log-skeleton-5/", //
		help = "Start Filter Notebook" //
) //
@Visualizer
public class ShowFilterNotebookPlugin {

	@UITopiaVariant( //
			affiliation = UITopiaVariant.EHV, //
			author = "H.M.W. Verbeek", //
			email = "h.m.w.verbeek@tue.nl" //
	) //
	@PluginVariant( //
			variantLabel = "Start Filter Notebook", //
			requiredParameterLabels = { 0, 1 } //
	) //
	public JComponent run(UIPluginContext context, final XLog log, final ProMCanceller canceller) {
		//		JComponent widget = new JPanel();
		//		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, TableLayoutConstants.FILL, TableLayoutConstants.FILL } };
		//		widget.setLayout(new TableLayout(size));
		//		
		//		ComputationCell computationCell = new ComputationCell(context, canceller, "Filter 1", log, "Notebook input log");
		//		widget.add(computationCell.getWidget(), "0, 0, 0, 1");
		//		
		//		TextCell textCell = new TextCell(context, "Description 1");
		//		widget.add(textCell.getWidget(), "0, 2");

		return new Notebook(context, canceller, log).getWidget();
	}

	@UITopiaVariant( //
			affiliation = UITopiaVariant.EHV, //
			author = "H.M.W. Verbeek", //
			email = "h.m.w.verbeek@tue.nl" //
	) //
	@PluginVariant( //
			variantLabel = "Visualize Filter Notebook", //
			requiredParameterLabels = { 2 } //
	) //
	public JComponent run(UIPluginContext context, final Notebook notebook) {
		return notebook.getWidget();
	}

}
