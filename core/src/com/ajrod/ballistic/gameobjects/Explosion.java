package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion extends Box{

	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame;
	private float stateTime;
	private float timer;
	
	public Explosion(float x, float y) {
		timer = 1;
		this.x = x;
		this.y = y;
		width = 0;
		height = 0;
		
		missileFrames = new TextureRegion[5];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("explosion").split(25, 25);
		for (int i = 0; i < 5; i++) missileFrames[i] = tmp[0][i];
		ani = new Animation(0.05f, missileFrames);
		stateTime = 0f;
	}
	
	public void update(float dt) {
		if (width != 0) {
			stateTime += dt;
			timer -= dt*4;
		}
		if (timer <= 0) reset();
	}
	
	public void render(SpriteBatch sb) {
		if (width != 0) {
			currentFrame = ani.getKeyFrame(stateTime);
			sb.draw(currentFrame, x - width/2, y - height/2, width, height);
		}
	}
	
	public void setWH(float radius) {
		width = radius + 10;
		height = radius + 10;
	}
	
	private void reset() {
		stateTime = 0f;
		timer = 1;
		width = 0;
		height = 0;
	}
	
	public void setXY(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
