/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.jfreechart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.util.LineUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * This is an XYLineAndShapeRenderer that allows to set a custom logic on whether
 * to draw lines between points during rendering.  This is a work around class until
 * the propsed changes is accepted on JFreeChart main repository
 *
 * @author aris
 */
public class XYLineAndShapeRenderer extends org.jfree.chart.renderer.xy.XYLineAndShapeRenderer {

	/**
	 * The logic that decides whether to draw lines between points during
	 * rendering. This makes it possible to have broken series lines when
	 * conditions are not met.
	 *
	 * For example: If you're charting an angle in degress from 0 to 360 and
	 * when it goes from 355 degrees to 361 degrees, you translated it back
	 * to 1 degrees instead of 361 degrees. This example above will result
	 * to a chart that have a sharp line connecting 355 degrees to 1
	 * degrees. If you don't wan't this, you can provide a
	 * drawLinesConditions logic that only draws line when the difference
	 * between current y-value and previous y-value is less than 180.
	 */
	protected DrawLinesCondition drawLinesCondition;

	public XYLineAndShapeRenderer() {
	}

	public XYLineAndShapeRenderer(boolean lines, boolean shapes) {
		super(lines, shapes);
	}

	/**
	 * Create a new renderer that has a custom logic for determining whether
	 * to draw lines between points.
	 *
	 * @param drawLinesCondition Custom logic for drawing lines between
	 * points
	 * @param shapes true, renders shapes in the chart.
	 */
	public XYLineAndShapeRenderer(DrawLinesCondition drawLinesCondition, boolean shapes) {
		this(true, shapes);
		this.drawLinesCondition = drawLinesCondition;
	}

	@Override
	protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {

		RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

		// get the data point...
		double x1 = dataset.getXValue(series, item);
		double y1 = dataset.getYValue(series, item);
		double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

		State s = (State) state;
		// update path to reflect latest point
		if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
			float x = (float) transX1;
			float y = (float) transY1;
			PlotOrientation orientation = plot.getOrientation();
			if (orientation == PlotOrientation.HORIZONTAL) {
				x = (float) transY1;
				y = (float) transX1;
			}
			if (s.isLastPointGood()) {
				double x0 = dataset.getXValue(series, item - 1);
				double y0 = dataset.getYValue(series, item - 1);
				if (getDrawLinesCondition().isDrawLine(y0, x0, y1, x1)) {
					s.seriesPath.lineTo(x, y);
				} else {
					s.seriesPath.moveTo(x, y);
				}
			} else {
				s.seriesPath.moveTo(x, y);
			}
			s.setLastPointGood(true);
		} else {
			s.setLastPointGood(false);
		}
		// if this is the last item, draw the path ...
		if (item == s.getLastItemIndex()) {
			// draw path
			drawFirstPassShape(g2, pass, series, item, s.seriesPath);
		}
	}

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

		if (!getDrawLinesCondition().isDrawLine(y0, x0, y1, x1)) {
			return;
		}

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

	/**
	 * Returns the custom logic for rendering lines between points.
	 *
	 * @see #setDrawLinesCondition(DrawLinesCondition)
	 *
	 * @return an instance of DrawLinesCondition.
	 */
	public DrawLinesCondition getDrawLinesCondition() {
		if (drawLinesCondition == null) {
			return DrawLinesCondition.ALWAYS_DRAW_LINES_CONDITION;
		}
		return drawLinesCondition;
	}

	/**
	 * Sets the custom logic for rendering lines between points.
	 * {@link RendererChangeEvent} to all registered listeners.
	 *
	 * @param drawLinesCondition an instance of DrawLinesCondition.
	 *
	 * @see #getBaseLinesVisible()
	 */
	public void setDrawLinesCondition(DrawLinesCondition drawLinesCondition) {
		this.drawLinesCondition = drawLinesCondition;
		setBaseLinesVisible(true);
		fireChangeEvent();
	}

	/**
	 * /**
	 * An interface for creating custom logic for drawing lines between
	 * points for XYLineAndShapeRenderer.
	 */
	public static interface DrawLinesCondition {

		public static DrawLinesCondition ALWAYS_DRAW_LINES_CONDITION = new DrawLinesCondition() {
			@Override
			public boolean isDrawLine(double y0, double x0, double y1, double x1) {
				return true;
			}
		};

		/**
		 * Custom logic for drawing lines between points.
		 *
		 * @param y0 previous y
		 * @param x0 previous x
		 * @param y1 current y
		 * @param x1 current x
		 * @return true, if you want to render a line between points.
		 * Otherwise, return false
		 */
		public boolean isDrawLine(double y0, double x0, double y1, double x1);
	}
}
