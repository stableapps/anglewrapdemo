/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.anglewraparounddemo;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.LineUtilities;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author aris
 */
public class AngleWrapDemoMain extends ApplicationFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new demo instance.
	 *
	 * @param title the frame title.
	 */
	public AngleWrapDemoMain(final String title) {
		super(title);
		final JFreeChart chart = createChart();
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
		setContentPane(chartPanel);
	}

	/**
	 * /**
	 * An interface for creating custom logic for drawing lines between
	 * points for XYLineAndShapeRenderer.
	 */
	public static interface OverflowCondition {

		/**
		 * Custom logic for detecting overflow between points.
		 *
		 * @param y0 previous y
		 * @param x0 previous x
		 * @param y1 current y
		 * @param x1 current x
		 * @return true, if you there is an overflow detected.
		 * Otherwise, return false
		 */
		public boolean isOverflow(double y0, double x0, double y1, double x1);
	}

	/**
	 * Creates a sample dataset.
	 *
	 * @param count the item count.
	 *
	 * @return the dataset.
	 */
	private XYDataset createAngleDataset(final int count) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries s1 = new TimeSeries("Angle (In Degrees)");
		RegularTimePeriod start = new Minute();
		double direction = 180.0;
		for (int i = 0; i < count; i++) {
			s1.add(start, direction);
			start = start.next();
			direction = direction + (Math.random() - 0.45) * 15.0;
			if (direction < 0.0) {
				direction = direction + 360.0;
			} else if (direction > 360.0) {
				direction = direction - 360.0;
			}
		}
		dataset.addSeries(s1);
		return dataset;
	}

	/**
	 * Creates a sample chart.
	 *
	 * @return a sample chart.
	 */
	private JFreeChart createChart() {
		final XYDataset direction = createAngleDataset(600);
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
			"Time",
			"Date",
			"Direction",
			direction,
			true,
			true,
			false
		);

		final XYPlot plot = chart.getXYPlot();
		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setUpperMargin(0.0);

		// configure the range axis to display directions...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRangeIncludesZero(false);
		plot.setRangeAxis(rangeAxis);

		final OverflowCondition overflowCondition = new OverflowCondition() {
			@Override
			public boolean isOverflow(double y0, double x0, double y1, double x1) {
				return Math.abs(y1 - y0) < 180;
			}
		};
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
			double min = 0;
			double max = 360;
			LinearInterpolator interpolator = new LinearInterpolator();

			@Override
			protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
				if (item == 0) {
					return;
				}

				// get the data point...
				double x1 = dataset.getXValue(series, item);
				double y1 = dataset.getYValue(series, item);
				if (Double.isNaN(y1) || Double.isNaN(x1)) {
					return;
				}

				double x0 = dataset.getXValue(series, item - 1);
				double y0 = dataset.getYValue(series, item - 1);
				if (Double.isNaN(y0) || Double.isNaN(x0)) {
					return;
				}

				if (!overflowCondition.isOverflow(y0, x0, y1, x1)) {
					boolean overflowAtMax = y1 < y0;
					if (overflowAtMax) {
						PolynomialSplineFunction psf = interpolator.interpolate(new double[]{y0, y1 + max}, new double[]{x0, x1});
						double xmid = psf.value(max);
						drawPrimaryLine(state, g2, plot, x0, y0, xmid, max, pass, series, item, domainAxis, rangeAxis, dataArea);
						drawPrimaryLine(state, g2, plot, xmid, min, x1, y1, pass, series, item, domainAxis, rangeAxis, dataArea);
					} else {
						PolynomialSplineFunction psf = interpolator.interpolate(new double[]{y1 - max, y0}, new double[]{x1, x0});
						double xmid = psf.value(min);
						drawPrimaryLine(state, g2, plot, x0, y0, xmid, min, pass, series, item, domainAxis, rangeAxis, dataArea);
						drawPrimaryLine(state, g2, plot, xmid, max, x1, y1, pass, series, item, domainAxis, rangeAxis, dataArea);
					}
				} else {
					drawPrimaryLine(state, g2, plot, x0, y0, x1, y1, pass, series, item, domainAxis, rangeAxis, dataArea);
				}

			}

			private void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, double x0, double y0, double x1, double y1, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
				RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
				RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
				double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
				double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
				double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
				double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
				// only draw if we have good values
				if (Double.isNaN(transX0) || Double.isNaN(transY0)
					|| Double.isNaN(transX1) || Double.isNaN(transY1)) {
					return;
				}
				PlotOrientation orientation = plot.getOrientation();
				boolean visible;
				if (orientation == PlotOrientation.HORIZONTAL) {
					state.workingLine.setLine(transY0, transX0, transY1, transX1);
				} else if (orientation == PlotOrientation.VERTICAL) {
					state.workingLine.setLine(transX0, transY0, transX1, transY1);
				}
				visible = LineUtilities.clipLine(state.workingLine, dataArea);
				if (visible) {
					drawFirstPassShape(g2, pass, series, item, state.workingLine);
				}
			}
		};
		plot.setRenderer(0, renderer);

		return chart;
	}

	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args ignored.
	 */
	public static void main(final String[] args) {
		final AngleWrapDemoMain demo = new AngleWrapDemoMain("Angle Wrap Demo");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
