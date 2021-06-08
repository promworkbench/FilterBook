package org.processmining.filterbook.parameters;

import org.processmining.filterbook.types.AttributeType;
import org.processmining.filterbook.types.AttributeValueType;
import org.processmining.filterbook.types.ClassifierType;
import org.processmining.filterbook.types.SelectionType;

public class Parameters {

	/*
	 * Exhaustive list of possible filter parameters.
	 * 
	 * Every filter has a dedicated collection of typed parameters. Parameters that are
	 * not used by a filter, can be ignored.
	 */

	/*
	 * Selected classifier.
	 */
	private OneFromListParameter<ClassifierType> oneFromListClassifier;
	/*
	 * Selected attribute.
	 */
	private OneFromListParameter<AttributeType> oneFromListAttribute;
	/*
	 * Selected String values, like classifier values.
	 */
	private MultipleFromListParameter<String> multipleFromListString;
	/*
	 * Selected attribute values.
	 */
	private MultipleFromListParameter<AttributeValueType> multipleFromListAttributeValue;
	/*
	 * First boolean option.
	 */
	private YesNoParameter yesNoA;
	/*
	 * Second boolean option.
	 */
	private YesNoParameter yesNoB;
	/*
	 * Selected selection (Filter in/Filter out)
	 */
	private OneFromListParameter<SelectionType> oneFromListSelection;
	/*
	 * Selected Integer values, like trace lengths.
	 */
	private MultipleFromListParameter<Integer> multipleFromListInteger;
	/*
	 * First Date option
	 */
	private DateParameter dateA;
	/*
	 * Second Data option
	 */
	private DateParameter dateB;
	/*
	 * First number option
	 */
	private NumberParameter numberA;
	
	/*
	 * Getters and setters for all parameters.
	 */

	public OneFromListParameter<ClassifierType> getOneFromListClassifier() {
		return oneFromListClassifier;
	}

	public void setOneFromListClassifier(OneFromListParameter<ClassifierType> oneFromListClassifier) {
		this.oneFromListClassifier = oneFromListClassifier;
	}

	public OneFromListParameter<AttributeType> getOneFromListAttribute() {
		return oneFromListAttribute;
	}

	public void setOneFromListAttribute(OneFromListParameter<AttributeType> oneFromListAttribute) {
		this.oneFromListAttribute = oneFromListAttribute;
	}

	public OneFromListParameter<SelectionType> getOneFromListSelection() {
		return oneFromListSelection;
	}

	public void setOneFromListSelection(OneFromListParameter<SelectionType> oneFromListSelection) {
		this.oneFromListSelection = oneFromListSelection;
	}

	public MultipleFromListParameter<String> getMultipleFromListString() {
		return multipleFromListString;
	}

	public void setMultipleFromListString(MultipleFromListParameter<String> multipleFromListString) {
		this.multipleFromListString = multipleFromListString;
	}

	public MultipleFromListParameter<AttributeValueType> getMultipleFromListAttributeValue() {
		return multipleFromListAttributeValue;
	}

	public void setMultipleFromListAttributeValue(
			MultipleFromListParameter<AttributeValueType> multipleFromListAttributeValue) {
		this.multipleFromListAttributeValue = multipleFromListAttributeValue;
	}

	public YesNoParameter getYesNoA() {
		return yesNoA;
	}

	public void setYesNoA(YesNoParameter yesNoA) {
		this.yesNoA = yesNoA;
	}

	public YesNoParameter getYesNoB() {
		return yesNoB;
	}

	public void setYesNoB(YesNoParameter yesNoB) {
		this.yesNoB = yesNoB;
	}

	public MultipleFromListParameter<Integer> getMultipleFromListInteger() {
		return multipleFromListInteger;
	}

	public void setMultipleFromListInteger(MultipleFromListParameter<Integer> multipleFromListInteger) {
		this.multipleFromListInteger = multipleFromListInteger;
	}

	public DateParameter getDateA() {
		return dateA;
	}

	public void setDateA(DateParameter dateA) {
		this.dateA = dateA;
	}

	public DateParameter getDateB() {
		return dateB;
	}

	public void setDateB(DateParameter dateB) {
		this.dateB = dateB;
	}

	public NumberParameter getNumberA() {
		return numberA;
	}

	public void setNumberA(NumberParameter numberA) {
		this.numberA = numberA;
	}

}
