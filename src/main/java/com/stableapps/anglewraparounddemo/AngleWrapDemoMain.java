/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.anglewraparounddemo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author aris
 */
public class AngleWrapDemoMain extends ApplicationFrame {

	/**
	 * Creates a new demo instance.
	 *
	 * @param title the frame title.
	 */
	public AngleWrapDemoMain(final String title) {
		super(title);
		final JFreeChart chart = createChart();
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/**
	 * Creates a sample dataset.
	 *
	 * @param count the item count.
	 *
	 * @return the dataset.
	 */
	private XYDataset createDirectionDataset(final int count) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries s1 = new TimeSeries("Wind Direction");
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
	 * Creates a sample dataset.
	 *
	 * @param count the item count.
	 *
	 * @return the dataset.
	 */
	private XYDataset createForceDataset(final int count) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries s1 = new TimeSeries("Wind Force");
		RegularTimePeriod start = new Minute();
		double force = 3.0;
		for (int i = 0; i < count; i++) {
			s1.add(start, force);
			start = start.next();
			force = Math.max(0.5, force + (Math.random() - 0.5) * 0.5);
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
		final XYDataset direction = createDirectionDataset(600);
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

		// add the wind force with a secondary dataset/renderer/axis
		plot.setRangeAxis(rangeAxis);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
		renderer.setDrawLinesCondition(new XYLineAndShapeRenderer.DrawLinesCondition() {
			@Override
			public boolean isDrawLine(double y1, double x1, double y2, double x2) {
				return Math.abs(y2-y1) < 180;
			}
		});

		final XYItemRenderer renderer2 = new XYAreaRenderer();
		final ValueAxis axis2 = new NumberAxis("Force");
		axis2.setRange(0.0, 12.0);
		renderer2.setSeriesPaint(0, new Color(0, 0, 255, 128));
		plot.setDataset(1, createForceDataset(600));
		plot.setRenderer(1, renderer2);
		plot.setRangeAxis(1, axis2);
		plot.mapDatasetToRangeAxis(1, 1);

		return chart;
	}

	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args ignored.
	 */
	public static void main(final String[] args) {
		final AngleWrapDemoMain demo = new AngleWrapDemoMain("Compass Format Demo");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
