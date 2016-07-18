/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.anglewraparounddemo;

/**
 * This is a utility class for doing linear interpolation.
 * @author aris
 */
public class LinearInterpolator {

	/**
	 * Creates a new instance of LinearFunction for computing linear interpolation
	 * @param x x values.  Must be of length == 2
	 * @param y y values.  Must be of length == 2
	 * @return an instance of LinearFunction
	 */
	public LinearFunction interpolate(double[] x, double[] y) {
		return new LinearFunction(x, y);
	}
	
}
