package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class LoadingMissile extends Circle {
	
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame;
	private final float MAX_RADIUS = 30;
	private float stateTime, timer, cdtimer;
	private Explosion explosion;
	private boolean cooldown;
	
	public LoadingMissile() {
		float[] coords = missileCoords();
		x = coords[0];
		y = coords[1];
		explosion = new Explosion(x, y);
		closeness = 0;
		radius = 0;
		
		missileFrames = new TextureRegion[3];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("basicMissile").split(25, 25);
		for (int i = 0; i < 3; i++) missileFrames[i] = tmp[0][i];
		ani = new Animation(0.1f, missileFrames);
		ani.setPlayMode(PlayMode.LOOP_PINGPONG);
		stateTime = 0f;
		timer = 0; cdtimer = 0.3f;
		cooldown = false;
	}
	
	public boolean onClick() { return false; }
	
	public void update(float dt) {
		explosion.update(dt);
		stateTime += dt;
		if (!cooldown) {
			radius += 1;
			closeness = (float)radius/1.6f;
			if (radius >= MAX_RADIUS) reset();
		}
		else {
			timer += dt;
			if (timer > cdtimer)
				cooldown = false;
		}
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		currentFrame = ani.getKeyFrame(stateTime, true);
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
	
	public void reset() {
		explosion.setWH((float)radius);
		radius = 0; timer = 0;
		closeness = 0;
		cooldown = true;
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }

	private float[] missileCoords() {
		float temp[] = {Ballistic.WIDTH/2, Ballistic.HEIGHT/2 - 10};
		return temp;
	}
	
	//private void warn(SpriteBatch sb) {}
}
