package com.ajrod.ballistic.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Graphic extends Box {

	private TextureRegion image;
	
	public Graphic(TextureRegion image, float x, float y) {
		this.image = image;
		this.x = x;
		this.y = y;
		width = image.getRegionWidth()+200;
		height = image.getRegionHeight()+200;
	}
	
	public void render(SpriteBatch sb) {
		sb.draw(image, x - width/2, y - height/2, width, height);
	}
}
