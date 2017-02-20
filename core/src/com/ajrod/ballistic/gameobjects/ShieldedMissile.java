package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import java.util.Random;

public class ShieldedMissile extends Circle {

	protected Animation ani;
	protected TextureRegion[] missileFrames;
	protected TextureRegion currentFrame;
	protected float stateTime;
	protected Explosion explosion;
	private final float MAX_RADIUS;
	private float timer, cdtimer;
	private boolean cooldown, invincible;
	private Random rand;
	private int hp;
	private TextureRegion warning;
	
	public ShieldedMissile(float radius) {
		hp = 2;
		rand = new Random();
		float[] coords = missileCoords();
		x = coords[0];
		y = coords[1];
		explosion = new Explosion(x, y);
		cooldown = true;
        invincible = true;
        closeness = 0;
		timer = 0; cdtimer = 3;
		MAX_RADIUS = radius;
		this.radius = 0;
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_basic");
		missileFrames = new TextureRegion[6];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("smissile").split(25, 25);
		int k = 0;
		for (int i = 0; i < 2; i++) 
			for (int j = 0; j < 3; j++) {
				missileFrames[k] = tmp[i][j];
				k++;
			}
		ani = new Animation(0.1f, missileFrames);
		ani.setPlayMode(PlayMode.LOOP_PINGPONG);
		stateTime = 0f;
	}
	
	public void onClick() {
		if (!invincible) {
			if (Ballistic.soundOn)
				Ballistic.sounds.hit.play(1.0f);
			hp--;
		}
		if (hp == 0) {
			if (Ballistic.soundOn)
				Ballistic.sounds.explosion.play(1.0f);
			reset();
		}
	}
	
	public void update(float dt, float speed) {
		explosion.update(dt);
		stateTime += dt;
		if (stateTime > 0.29f) stateTime = 0f;
		if (!cooldown) {
			radius += dt*speed;
			closeness = (float)radius/1.6f;
		}
		else {
			timer += dt;
			if (timer > cdtimer + rand.nextFloat()) {
				explosion.setXY(x, y);
				cooldown = false;
                invincible = false;
				if (cdtimer > 1f) cdtimer -= dt;
			}
		}
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		if (hp == 2) currentFrame = ani.getKeyFrame(stateTime, true);
		else currentFrame = ani.getKeyFrame(stateTime + 0.3f, true);
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		warn(sb);
	}
	
	public void reset() {
		explosion.setWH((float)radius);
		radius = 0; timer = 0;
		hp = 2; closeness = 0;
        invincible = true;
		cooldown = true;
		float[] coords = missileCoords();
		x = coords[0]; y = coords[1];
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }
	
	public int getHP() { return hp; }
	
	private float[] missileCoords() {
		float temp[] = new float[2];
		if (Ballistic.onMenu) {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 80) + 40;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 280) + 240;
		}
		else {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 80) + 40;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 180) + 90;
		}
		return temp;
	}
	
	private void warn(SpriteBatch sb) {
		if ((radius >= 70 && radius <= 71)
			|| (radius >= 73 && radius <= 74)
			|| (radius >= 76 && radius <= 77))
			sb.draw(warning, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
}
