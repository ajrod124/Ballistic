package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import java.util.Random;

public class SpiralMissile extends Circle {
	
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame;
	private final float MAX_RADIUS, startAngle;
	private float timer, cdtimer, t, angle;
	private boolean cooldown, invincible, moving;
	private Random rand;
	private float stateTime;
	private Explosion explosion;
	private TextureRegion warning;
	
	public SpiralMissile(float radius) {
		moving = false;
		rand = new Random();
		float[] coords = missileCoords();
		x = coords[0];
		y = coords[1];
		explosion = new Explosion(x, y);
		cooldown = true;
        invincible = true;
        closeness = 0;
		timer = 0; cdtimer = 5; t = 20;
		angle = (3600/(float)Math.PI) + 270f;
		startAngle = (3600/(float)Math.PI) + 270f;
		MAX_RADIUS = radius;
		this.radius = 0;
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_basic");
		missileFrames = new TextureRegion[6];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("spiralMissile").split(25, 25);
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
	
	public boolean onClick() {
        if (!invincible) {
        	if (Ballistic.soundOn)
        		Ballistic.sounds.explosion.play(1.0f);
        	reset();
        	return true;
        }
        return false;
    }
	
	public void update(float dt, float speed) {
		explosion.update(dt);
		stateTime += dt;
		if (stateTime > 0.29f) stateTime = 0;
		if (!cooldown) {
			if (t <= 0) {
				if (moving) moving = false;
				radius += dt*speed;
				closeness = (float)radius/1.6f;
			}
			else {
				if (radius >= 15) {
					if (!moving) moving = true;
					move(dt, speed/4);
				}
				else {
					if (moving) moving = false;
					radius += dt*speed;
					closeness = (float)radius/1.6f;
				}
			}
		}
		else {
			timer += dt;
			if (timer > cdtimer + (rand.nextInt()%3 + 1)) {
				explosion.setXY(x, y);
				cooldown = false;
                invincible = false;
				if (cdtimer > 3f) cdtimer -= dt;
			}
		}
	}
	
	private void move(float dt, float speed) {
		x += t*Math.cos(t)/(10/speed);
		y += t*Math.sin(t)/(10/speed);
		t -= dt*speed;
		angle -= speed;
	}

	public void render(SpriteBatch sb) {
		explosion.render(sb);
		if (!moving) {
			currentFrame = ani.getKeyFrame(stateTime, true);
			sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		}
		else {
			currentFrame = ani.getKeyFrame(stateTime + 0.3f, true);
			sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius/2,  (float)radius/2, (float)radius, (float)radius, 2f, 2f, angle%360);
		}
		warn(sb);
	}
	
	public void reset() {
		t = 20;
		explosion.setXY(x, y);
		explosion.setWH((float)radius);
		radius = 0; timer = 0;
		closeness = 0;
        invincible = true;
		cooldown = true;
		angle = startAngle;
		float[] coords = missileCoords();
		x = coords[0]; y = coords[1];
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	public double getRadius() { return (float)radius; }

	private float[] missileCoords() {
		float temp[] = new float[2];
		if (Ballistic.onMenu) {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 240);
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 500) + 400;
		}
		else {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 240);
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 300) + 200;
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
