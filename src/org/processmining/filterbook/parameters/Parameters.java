package org.processmining.filterbook.parameters;

import java.time.Duration;

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
	private MultipleFromListParameter<String> multipleFromListStringA;
	private MultipleFromListParameter<String> multipleFromListStringB;
	/*
	 * Selected attribute values.
	 */
	private MultipleFromListParameter<AttributeValueType> multipleFromListAttributeValueA;
	private MultipleFromListParameter<AttributeValueType> multipleFromListAttributeValueB;
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
	 * Selected Integer values, like trace lengths.
	 */
	private MultipleFromListParameter<Duration> multipleFromListDuration;
	
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

	public MultipleFromListParameter<String> getMultipleFromListStringA() {
		return multipleFromListStringA;
	}

	public void setMultipleFromListStringA(MultipleFromListParameter<String> multipleFromListStringA) {
		this.multipleFromListStringA = multipleFromListStringA;
	}

	public MultipleFromListParameter<String> getMultipleFromListStringB() {
		return multipleFromListStringB;
	}

	public void setMultipleFromListStringB(MultipleFromListParameter<String> multipleFromListStringB) {
		this.multipleFromListStringB = multipleFromListStringB;
	}

	public MultipleFromListParameter<AttributeValueType> getMultipleFromListAttributeValueA() {
		return multipleFromListAttributeValueA;
	}

	public void setMultipleFromListAttributeValueA(
			MultipleFromListParameter<AttributeValueType> multipleFromListAttributeValueA) {
		this.multipleFromListAttributeValueA = multipleFromListAttributeValueA;
	}

	public MultipleFromListParameter<AttributeValueType> getMultipleFromListAttributeValueB() {
		return multipleFromListAttributeValueB;
	}

	public void setMultipleFromListAttributeValueB(
			MultipleFromListParameter<AttributeValueType> multipleFromListAttributeValueB) {
		this.multipleFromListAttributeValueB = multipleFromListAttributeValueB;
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

	public MultipleFromListParameter<Duration> getMultipleFromListDuration() {
		return multipleFromListDuration;
	}

	public void setMultipleFromListDuration(MultipleFromListParameter<Duration> multipleFromListDuration) {
		this.multipleFromListDuration = multipleFromListDuration;
	}

}
