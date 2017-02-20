package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SoundButton extends Box {
	
	private TextureRegion[] region;
	private boolean soundOn;
	
	public SoundButton(float x, float y, float w, float h, boolean soundOn) {
		
		region = new TextureRegion[2];
		
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("soundButton").split(50, 50);
		for (int i = 0; i < 2; i++) region[i] = tmp[0][i];
		
		this.soundOn = soundOn;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public void render(SpriteBatch sb) {
		if (soundOn)
			sb.draw(region[0], x - width/2, y - height/2, width, height);
		else
			sb.draw(region[1], x - width/2, y - height/2, width, height);
	}
	
	public void setState(boolean b) { soundOn = b; }
}
