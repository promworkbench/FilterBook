package org.processmining.filterbook.types;

import org.deckfour.xes.classification.XEventClassifier;

public class ClassifierType implements Comparable<ClassifierType> {

	/*
	 * Wrapper class for classifiers.
	 */
	
	/**
	 * The classifier for this type. Never null.
	 */
	private XEventClassifier classifier;
	
	/**
	 * Create a classifier type from a classifier from the log.
	 * @param classifier The classifier from the log.
	 */
	public ClassifierType(XEventClassifier classifier) {
		this.classifier = classifier;
	}
	
	/**
	 * Get the classifier for this classifier type.
	 * @return THe classifier for this classifier type.
	 */
	public XEventClassifier getClassifier() {
		return classifier;
	}
	
	/**
	 * Name for the classifier type. Use classifier name.
	 */
	public String toString() {
		return classifier.name();
	}
	
	/**
	 * Make the classifier type comparable. 
	 */
	public int compareTo(ClassifierType o) {
		return toString().compareTo(o.toString());
	}
	
	public boolean equals(Object o) {
		if (o instanceof ClassifierType) {
			return toString().equals(o.toString());
		}
		return false;
	}
}
