package org.processmining.filterbook.plugins;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.filterbook.notebook.NotebookTemplate;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.w3c.dom.Document;

@Plugin(name = "NBXML export (Notebook template)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Notebook template", "File" }, userAccessible = true)
@UIExportPlugin(description = "Notebook template", extension = "nbxml")
public class ExportNotebookTemplatePlugin {

	@PluginVariant(variantLabel = "NBXML export (Notebook template)", requiredParameterLabels = { 0, 1 })
	public void export(PluginContext context, NotebookTemplate notebookTemplate, File file) throws IOException {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			notebookTemplate.exportToDocument(document);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(domSource, streamResult);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
