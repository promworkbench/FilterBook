package org.processmining.filterbook.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.filterbook.notebook.NotebookTemplate;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin( //
		name = "Create notebook from template", //
		parameterLabels = { "Notebook Template", "Event log" }, //
		returnLabels = { "Notebook" }, //
		returnTypes = { Notebook.class }, //
		userAccessible = true, //
		icon = "prom_duck.png", //
		url = "http://www.win.tue.nl/~hverbeek/", //
		help = "" //
) //
public class CreateFilterNotebookPlugin {

	@UITopiaVariant( //
			affiliation = UITopiaVariant.EHV, //
			author = "H.M.W. Verbeek", //
			email = "h.m.w.verbeek@tue.nl" //
	) //
	@PluginVariant( //
			variantLabel = "Create notebook from template", //
			requiredParameterLabels = { 0, 1 } //
	) //
	public Notebook create(UIPluginContext context, NotebookTemplate notebookTemplate, XLog log) {
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		Notebook notebook = new Notebook(context, canceller, log);
		notebook.populate(context, canceller, notebookTemplate, log);
		return notebook;
	}
}
