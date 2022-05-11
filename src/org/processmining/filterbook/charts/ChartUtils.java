package org.processmining.filterbook.charts;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.TableOrder;

public class ChartUtils {

	private final static Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
	private final static BarPainter BAR_PAINTER = new StandardBarPainter();
	public final static int TOO_MANY_VALUES = 10000;

	public static JFreeChart createMultiplePieChart(String title, CategoryDataset dataset, TableOrder order) {
		JFreeChart chart = ChartFactory.createMultiplePieChart(title, dataset, order, true, true, false);
		// Make the background transparent.
		chart.setBackgroundPaint(TRANSPARENT_COLOR);
		MultiplePiePlot plot = (MultiplePiePlot) chart.getPlot();
		plot.setBackgroundPaint(TRANSPARENT_COLOR);
		plot.getPieChart().setBackgroundPaint(TRANSPARENT_COLOR);
		return chart;
	}

	public static JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel,
			CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset,
				PlotOrientation.HORIZONTAL, true, true, false);
		// Make the background transparent.
		chart.setBackgroundPaint(TRANSPARENT_COLOR);
		// Remove the gradient
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBarPainter(BAR_PAINTER);
		return chart;
	}
}
