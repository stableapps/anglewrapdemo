/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.anglewraparounddemo;

/**
 *
 * This class do linear interpolation
 * @author aris
 */
public class LinearFunction {

	private final double[] xs;
	private final double[] ys;

	/**
	 * Creates a new instance of LinearFunction with the given x array and y array.
	 * Please see {@code LinearInterpolator.java}
	 * @param x x values.  Must be of length == 2
	 * @param y y values.  Must be of length == 2
	 */
	public LinearFunction(double[] x, double[] y) {
		if (x.length != 2 || y.length != 2){
			throw new IllegalArgumentException("x and y need to be an array of length == 2");
		}
		if (x[0] > x[1]){
			throw new IllegalArgumentException("x needs to be increasing value");
		}
		this.xs = x;
		this.ys = y;
	}

	public double value(double x) {
		if (x < xs[0] || x > xs[1]){
			throw new IllegalArgumentException(String.format("x needs to be between %s and %s", xs[0], xs[1]));
		}
		return ys[0]+(ys[1]-ys[0])*(x-xs[0])/(xs[1]-xs[0]);
	}
	
}
