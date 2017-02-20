package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import java.util.Random;

public class BasicMissile extends Circle {
	
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame;
	private final float MAX_RADIUS;
	private float timer, cdtimer;
	private boolean cooldown, invincible;
	private Random rand;
	private boolean boss;
	private float stateTime;
	private Explosion explosion;
	private TextureRegion warning;
	
	public BasicMissile(float radius, boolean boss) {
		rand = new Random();
		float[] coords = missileCoords(0f);
		x = coords[0];
		y = coords[1];
		explosion = new Explosion(x, y);
		closeness = 0;
		cooldown = true;
        invincible = true;
		this.boss = boss;
		timer = 0; cdtimer = 1;
		MAX_RADIUS = radius;
		this.radius = 0;
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_basic");
		missileFrames = new TextureRegion[3];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("basicMissile").split(25, 25);
		for (int i = 0; i < 3; i++) missileFrames[i] = tmp[0][i];
		ani = new Animation(0.1f, missileFrames);
		ani.setPlayMode(PlayMode.LOOP_PINGPONG);
		stateTime = 0f;
	}
	
	public boolean onClick(float bossRadius) {
        if (!invincible) {
        	if (Ballistic.soundOn)
        		Ballistic.sounds.explosion.play(1.0f);
        	reset(bossRadius);
        	return true;
        }
        return false;
    }
	
	public void update(float dt, float speed) {
		explosion.update(dt);
		stateTime += dt;
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
				if (cdtimer > 0.5f) cdtimer -= dt;
			}
		}
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		currentFrame = ani.getKeyFrame(stateTime, true);
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		warn(sb);
	}
	
	public void reset(float bossRadius) {
		explosion.setWH((float)radius);
		radius = 0; timer = 0;
		closeness = 0;
        invincible = true;
		cooldown = true;
		float[] coords = missileCoords(bossRadius);
		x = coords[0]; y = coords[1];
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }

	private float[] missileCoords(float bossRadius) {
		float temp[] = new float[2];
		if (Ballistic.onMenu) {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 80) + 40;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 280) + 240;
		}
		else {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 80) + 40;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 180) + 90;
		}
		if (boss) {
			while (bossContains(temp[0], temp[1], bossRadius)) {
				temp[0] = rand.nextInt(Ballistic.WIDTH - 20) + 10;
				temp[1] = rand.nextInt(Ballistic.HEIGHT - 180) + 90;
			}
				
		}
		return temp;
	}
	
	private void warn(SpriteBatch sb) {
		if ((radius >= 70 && radius <= 71)
			|| (radius >= 73 && radius <= 74)
			|| (radius >= 76 && radius <= 77))
			sb.draw(warning, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
	
	private boolean bossContains(float x, float y, float bossRadius) { return r(x, y) <= (bossRadius/2 + 15); }
	
	private double r(float x, float y) {
		
		float tmpX = Ballistic.WIDTH/2 - x;
		float tmpY = Ballistic.HEIGHT/2 - y;
		
		return Math.sqrt(tmpX*tmpX + tmpY*tmpY);
	}
	
}
