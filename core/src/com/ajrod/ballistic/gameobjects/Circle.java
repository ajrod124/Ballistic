package com.ajrod.ballistic.gameobjects;

public class Circle{
	
	protected float x, y; 
	protected double radius;
	protected float closeness;
	
	public boolean contains(float x, float y) { return r(x, y) <= (radius/2 + 15); }
	
	private double r(float x, float y) {
		
		float tmpX = this.x - x;
		float tmpY = this.y - y;
		
		return Math.sqrt(tmpX*tmpX + tmpY*tmpY);
	}
	
	public float getCloseness() { return closeness; }
	public float getX() { return x; }
	public float getY() { return y; }
}
