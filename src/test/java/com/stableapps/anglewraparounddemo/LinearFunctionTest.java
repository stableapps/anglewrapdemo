/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.anglewraparounddemo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aris
 */
public class LinearFunctionTest {

	public LinearFunctionTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testIncreasingValue() {
		LinearFunction instance = new LinearFunction(new double[]{1, 2}, new double[]{2, 3});
		try {
			instance.value(0.0);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		try {
			instance.value(2.5);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(2.0, instance.value(1.0), 0.01);
		assertEquals(2.5, instance.value(1.5), 0.01);
		assertEquals(3.0, instance.value(2.0), 0.01);
	}

	@Test
	public void testDecreasingValue() {
		LinearFunction instance = new LinearFunction(new double[]{1, 2}, new double[]{3, 2});
		try {
			instance.value(0.0);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		try {
			instance.value(2.5);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(3.0, instance.value(1.0), 0.01);
		assertEquals(2.5, instance.value(1.5), 0.01);
		assertEquals(2.0, instance.value(2.0), 0.01);
	}

	@Test
	public void testNegativeValueIncreasing() {
		LinearFunction instance = new LinearFunction(new double[]{1, 2}, new double[]{-3, -2});
		try {
			instance.value(0.0);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		try {
			instance.value(2.5);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(-3.0, instance.value(1.0), 0.01);
		assertEquals(-2.5, instance.value(1.5), 0.01);
		assertEquals(-2.0, instance.value(2.0), 0.01);
	}

	@Test
	public void testNegativeValueDecreasing() {
		LinearFunction instance = new LinearFunction(new double[]{1, 2}, new double[]{-2, -3});
		try {
			instance.value(0.0);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		try {
			instance.value(2.5);
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(-2.0, instance.value(1.0), 0.01);
		assertEquals(-2.5, instance.value(1.5), 0.01);
		assertEquals(-3.0, instance.value(2.0), 0.01);
	}

	@Test
	public void testNonIncreasingX() {
		try {
			LinearFunction instance = new LinearFunction(new double[]{2, 1}, new double[]{-2, -3});
			fail("IllegalArgumentException should be thrown");
		} catch (IllegalArgumentException e) {
		}
	}

}
