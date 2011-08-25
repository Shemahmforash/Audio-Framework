/**
 * 
 */
package com.icdif.audio.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * @author wanderer
 * 
 */
public class Chart extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Chart(String title, ArrayList<Float> values) {
		super(title);
				
		XYDataset dataset = createDataset(values, "la la");

		JFreeChart chart = createChart(dataset, title);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	private XYDataset createDataset(ArrayList<Float> values, String seriesName) {
		XYSeries series1 = new XYSeries(seriesName);

		for (int i = 0; i < values.size(); i++) {
			series1.add(i, values.get(i));
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);

		return dataset;
	}

	private XYDataset createDataset() {
		XYSeries series1 = new XYSeries("First");

		Random generator = new Random();

		for (int i = 0; i < 1000; i++) {
			double rnd = generator.nextInt(100);
			series1.add(i, rnd);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);

		return dataset;
	}

	private JFreeChart createChart(XYDataset dataset, String title) {
		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(title,
		// chart title
				"X",
				// x axis label
				"Y",
				// y axis label
				dataset,
				// data
				PlotOrientation.VERTICAL, true,
				// include legend
				true,
				// tooltips
				false // urls
				);
		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);
		// get a reference to the plot for further customisation...
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer();

		// renderer.setShapesVisible(true);
		// renderer.setShapesFilled(true);

		// change the auto tick unit selection to integer units only...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.
		return chart;

	}

}
