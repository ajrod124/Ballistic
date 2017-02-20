package com.ajrod.ballistic.gameobjects;

public class Box {
	
	protected float x, y, width, height;
	
	public boolean contains(float x, float y) {
		return x > this.x - width/2 &&
				x < this.x + width/2 &&
				y > this.y - height/2 &&
				y < this.y + height/2;
	}
}
