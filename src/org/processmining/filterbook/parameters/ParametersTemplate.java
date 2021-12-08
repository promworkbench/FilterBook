package org.processmining.filterbook.parameters;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.deckfour.xes.util.XsDateTimeConversion;
import org.deckfour.xes.util.XsDateTimeConversionJava7;
import org.processmining.framework.util.HTMLToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ParametersTemplate implements HTMLToString {

	protected XsDateTimeConversion xsDateTimeConversion = new XsDateTimeConversionJava7();

	/*
	 * For the classifier.
	 */
	private String classifier;
	/*
	 * For the attribute.
	 */
	private String attribute;
	/*
	 * For the selected values.
	 */
	private Set<String> valuesA;
	private Set<String> valuesB;
	/*
	 * For the filter in/filter out.
	 */
	private String selection;
	/*
	 * For the first boolean option.
	 */
	private Boolean yesNoA;
	/*
	 * For the second boolean option.
	 */
	private Boolean yesNoB;
	/*
	 * For the first date option.
	 */
	private Date dateA;
	/*
	 * For the second date option.
	 */
	private Date dateB;
	/*
	 * For the first number option.
	 */
	private Integer numberA;

	/*
	 * Getters and setters.
	 */
	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * @deprecated Use getValuesA() instead.
	 * @return
	 */
	public Set<String> getValues() {
		return valuesA;
	}

	/**
	 * @deprecated Use setValuesA(values) instead.
	 * @param values
	 */
	public void setValues(Set<String> values) {
		this.valuesA = values;
	}

	public Set<String> getValuesA() {
		return valuesA;
	}

	public void setValuesA(Set<String> valuesA) {
		this.valuesA = valuesA;
	}

	public Set<String> getValuesB() {
		return valuesB;
	}

	public void setValuesB(Set<String> valuesB) {
		this.valuesB = valuesB;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public boolean isYesNoA() {
		return yesNoA;
	}

	public void setYesNoA(boolean yesNoA) {
		this.yesNoA = yesNoA;
	}

	public boolean isYesNoB() {
		return yesNoB;
	}

	public void setYesNoB(boolean yesNoB) {
		this.yesNoB = yesNoB;
	}

	public Date getDateA() {
		return dateA;
	}

	public void setDateA(Date dateA) {
		this.dateA = dateA;
	}

	public Date getDateB() {
		return dateB;
	}

	public void setDateB(Date dateB) {
		this.dateB = dateB;
	}

	/**
	 * Visualizes the template as HTML.
	 */
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buf = new StringBuffer();
		if (includeHTMLTags) {
			buf.append("<html>");
		}
		if (classifier != null) {
			buf.append("<li>Classifier: " + StringEscapeUtils.escapeHtml4(classifier) + "</li>");
		}
		if (attribute != null) {
			buf.append("<li>Attribute: " + StringEscapeUtils.escapeHtml4(attribute) + "</li>");
		}
		if (valuesA != null) {
			buf.append("<li>ValuesA: " + StringEscapeUtils.escapeHtml4(valuesA.toString()) + "</li>");
		}
		if (valuesB != null) {
			buf.append("<li>ValuesB: " + StringEscapeUtils.escapeHtml4(valuesB.toString()) + "</li>");
		}
		if (selection != null) {
			buf.append("<li>Selection: " + StringEscapeUtils.escapeHtml4(selection) + "</li>");
		}
		if (yesNoA != null) {
			buf.append("<li>YesNoA: " + StringEscapeUtils.escapeHtml4(yesNoA.toString()) + "</li>");
		}
		if (yesNoB != null) {
			buf.append("<li>YesNoB: " + StringEscapeUtils.escapeHtml4(yesNoB.toString()) + "</li>");
		}
		if (dateA != null) {
			buf.append("<li>DateA: " + StringEscapeUtils.escapeHtml4(dateA.toString()) + "</li>");
		}
		if (dateB != null) {
			buf.append("<li>DateB: " + StringEscapeUtils.escapeHtml4(dateB.toString()) + "</li>");
		}
		if (numberA != null) {
			buf.append("<li>NumberA: " + StringEscapeUtils.escapeHtml4(numberA.toString()) + "</li>");
		}
		if (includeHTMLTags) {
			buf.append("</html>");
		}
		return buf.toString();
	}

	/**
	 * Exports the template to a XML document.
	 * 
	 * @param document
	 *            The XML document
	 * @param filterElement
	 *            The filter element holding the parameters in the document.
	 */
	public void exportToDocument(Document document, Element filterElement) {
		if (classifier != null) {
			Element classifierElement = document.createElement("classifier");
			classifierElement.appendChild(document.createTextNode(classifier));
			filterElement.appendChild(classifierElement);
		}
		if (attribute != null) {
			Element attributeElement = document.createElement("attribute");
			attributeElement.appendChild(document.createTextNode(attribute));
			filterElement.appendChild(attributeElement);
		}
		if (valuesA != null) {
			for (String value : valuesA) {
				Element valueElement = document.createElement("valueA");
				filterElement.appendChild(valueElement);
				valueElement.appendChild(document.createTextNode(value));
			}
		}
		if (valuesB != null) {
			for (String value : valuesB) {
				Element valueElement = document.createElement("valueB");
				filterElement.appendChild(valueElement);
				valueElement.appendChild(document.createTextNode(value));
			}
		}
		if (selection != null) {
			Element selectionElement = document.createElement("selection");
			selectionElement.appendChild(document.createTextNode(selection.toString()));
			filterElement.appendChild(selectionElement);
		}
		if (yesNoA != null) {
			Element yesNoAElement = document.createElement("yesNoA");
			yesNoAElement.appendChild(document.createTextNode(yesNoA.toString()));
			filterElement.appendChild(yesNoAElement);
		}
		if (yesNoB != null) {
			Element yesNoBElement = document.createElement("yesNoB");
			yesNoBElement.appendChild(document.createTextNode(yesNoB.toString()));
			filterElement.appendChild(yesNoBElement);
		}
		if (dateA != null) {
			Element dateAElement = document.createElement("dateA");
			dateAElement.appendChild(document.createTextNode(xsDateTimeConversion.format(dateA)));
			filterElement.appendChild(dateAElement);
		}
		if (dateB != null) {
			Element dateBElement = document.createElement("dateB");
			dateBElement.appendChild(document.createTextNode(xsDateTimeConversion.format(dateB)));
			filterElement.appendChild(dateBElement);
		}
		if (numberA != null) {
			Element numberAElement = document.createElement("numberA");
			numberAElement.appendChild(document.createTextNode(numberA.toString()));
			filterElement.appendChild(numberAElement);
		}
	}

	/**
	 * Imports the template from an XML document.
	 * 
	 * @param document
	 *            The XML document
	 * @param filterElement
	 *            The filter element containing the template data.
	 */
	public void importFromDocument(Document document, Element filterElement) {
		NodeList nodes = filterElement.getElementsByTagName("classifier");
		if (nodes.getLength() >= 1) {
			setClassifier(nodes.item(0).getTextContent());
		}
		nodes = filterElement.getElementsByTagName("attribute");
		if (nodes.getLength() >= 1) {
			setAttribute(nodes.item(0).getTextContent());
		}
		nodes = filterElement.getElementsByTagName("valueA");
		if (nodes.getLength() > 0) {
			valuesA = new TreeSet<String>();
			for (int i = 0; i < nodes.getLength(); i++) {
				valuesA.add(nodes.item(i).getTextContent());
			}
		} else {
			nodes = filterElement.getElementsByTagName("value");
			if (nodes.getLength() > 0) {
				valuesA = new TreeSet<String>();
				for (int i = 0; i < nodes.getLength(); i++) {
					valuesA.add(nodes.item(i).getTextContent());
				}
			}
		}
		nodes = filterElement.getElementsByTagName("valueB");
		if (nodes.getLength() > 0) {
			valuesB = new TreeSet<String>();
			for (int i = 0; i < nodes.getLength(); i++) {
				valuesB.add(nodes.item(i).getTextContent());
			}
		}
		nodes = filterElement.getElementsByTagName("selection");
		if (nodes.getLength() >= 1) {
			setSelection(nodes.item(0).getTextContent());
		}
		nodes = filterElement.getElementsByTagName("yesNoA");
		if (nodes.getLength() >= 1) {
			setYesNoA(nodes.item(0).getTextContent().equalsIgnoreCase("true"));
		}
		nodes = filterElement.getElementsByTagName("yesNoB");
		if (nodes.getLength() >= 1) {
			setYesNoB(nodes.item(0).getTextContent().equalsIgnoreCase("true"));
		}
		nodes = filterElement.getElementsByTagName("dateA");
		if (nodes.getLength() >= 1) {
			setDateA(xsDateTimeConversion.parseXsDateTime(nodes.item(0).getTextContent()));
		}
		nodes = filterElement.getElementsByTagName("dateB");
		if (nodes.getLength() >= 1) {
			setDateB(xsDateTimeConversion.parseXsDateTime(nodes.item(0).getTextContent()));
		}
		nodes = filterElement.getElementsByTagName("numberA");
		if (nodes.getLength() >= 1) {
			setNumberA(new Integer(nodes.item(0).getTextContent()));
		}
	}

	public Integer getNumberA() {
		return numberA;
	}

	public void setNumberA(Integer numberA) {
		this.numberA = numberA;
	}
}
