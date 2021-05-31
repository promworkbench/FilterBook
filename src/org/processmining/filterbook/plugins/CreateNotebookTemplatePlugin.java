package org.processmining.filterbook.plugins;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.filterbook.notebook.NotebookTemplate;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin( //
		name = "Create notebook template", //
		parameterLabels = { "Notebook" }, //
		returnLabels = { "Notebook template" }, //
		returnTypes = { NotebookTemplate.class }, //
		userAccessible = true, //
		icon = "prom_duck.png", //
		url = "http://www.win.tue.nl/~hverbeek/", //
		help = "" //
) //
public class CreateNotebookTemplatePlugin {

	@UITopiaVariant( //
			affiliation = UITopiaVariant.EHV, //
			author = "H.M.W. Verbeek", //
			email = "h.m.w.verbeek@tue.nl" //
	) //
	@PluginVariant( //
			variantLabel = "Create notebook template", //
			requiredParameterLabels = { 0 } //
	) //
	public NotebookTemplate filter(PluginContext context, Notebook notebook) {
		return notebook.getTemplate();
	}
}
