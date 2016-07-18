/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.anglewraparounddemo;

/**
 * This an immutable class for containing two values together.
 * @author aris
 */
public class ImmutablePair<T0, T1> {
	protected final T0 left;
	protected final T1 right;

	public ImmutablePair(T0 left, T1 right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * @return the left value of the pair
	 */
	public  T0 getLeft() {
		return left;
	}

	/**
	 * @return the right value of the pair
	 */
	public T1 getRight() {
		return right;
	}
}
