package org.processmining.filterbook.parameters;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.filterbook.filters.Filter;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class NumberParameter extends Parameter implements ChangeListener {

	private Integer number;
	private Integer low;
	private Integer high;
	private NiceSlider slider;
	
	public NumberParameter() {

	}

	public NumberParameter(String label, Filter filter, Integer number, Integer low, Integer high) {
		super(label, filter);
		this.setNumber(number);
		this.setLow(low);
		this.setHigh(high);
	}

	
	public JComponent getWidget() {
		JComponent widget = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));
		widget.add(new JLabel(getLabel()), "0, 0");
		slider = SlickerFactory.instance().createNiceIntegerSlider("Select number", low, high,
				number, Orientation.HORIZONTAL);
		slider.remove(0); // Remove "Select number" label, we already have a label.
		slider.addChangeListener(this);
		widget.add(slider, "0, 1");
		return widget;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getLow() {
		return low;
	}

	public void setLow(Integer low) {
		this.low = low;
	}

	public Integer getHigh() {
		return high;
	}

	public void setHigh(Integer high) {
		this.high = high;
	}

	public void stateChanged(ChangeEvent arg0) {
		setNumber(slider.getSlider().getValue());
		getFilter().updated(this);
	}

}
