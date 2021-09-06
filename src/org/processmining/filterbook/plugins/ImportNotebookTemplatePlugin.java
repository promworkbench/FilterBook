package org.processmining.filterbook.plugins;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.filterbook.notebook.NotebookTemplate;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.w3c.dom.Document;

@Plugin( //
		name = "Import Notebook template from NBXML file", //
		level = PluginLevel.PeerReviewed, //
		parameterLabels = { "Filename" }, //
		returnLabels = { "Notebook template" }, //
		returnTypes = { NotebookTemplate.class } //
) //
@UIImportPlugin(description = "NBXML Notebook template files", extensions = { "nbxml" })
public class ImportNotebookTemplatePlugin extends AbstractImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("NBXML Notebook template files", "nbxml");
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		NotebookTemplate notebookTemplate = new NotebookTemplate();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
		DocumentBuilder db = dbf.newDocumentBuilder();  
		Document document = db.parse(input);  
		document.getDocumentElement().normalize();  
		notebookTemplate.importFromDocument(document);
		return notebookTemplate;
	}
}
