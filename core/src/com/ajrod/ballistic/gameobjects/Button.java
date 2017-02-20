package com.ajrod.ballistic.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Button extends Box{

	private TextureRegion button;
	
	public Button(TextureRegion region, float x, float y, float w, float h) {
		this.button = region;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public void render(SpriteBatch sb) {
		sb.draw(button, x - width/2, y - height/2, width, height);
	}
}
