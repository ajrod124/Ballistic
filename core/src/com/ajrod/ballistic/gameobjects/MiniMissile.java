package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class MiniMissile extends Circle {
	
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame;
	private final float MAX_RADIUS;
	private int direction, iteration;
	private boolean cooldown, invincible;
	private float stateTime;
	private Explosion explosion;
	private TextureRegion warning;
	
	public MiniMissile(float radius, float x, float y, int direction) {
		this.x = x;
		this.y = y;
		explosion = new Explosion(x, y);
		cooldown = true;
        invincible = true;
		iteration = 0;
		closeness = 0;
		this.direction = direction;
		MAX_RADIUS = radius;
		this.radius = 0;
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_mini");
		missileFrames = new TextureRegion[3];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("miniMissile").split(15, 15);
		for (int i = 0; i < 3; i++) missileFrames[i] = tmp[0][i];
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
		
		if (!cooldown) {
			explosion.setXY(x, y);
			if (iteration < 10) {
				move(dt);
				iteration++;
			}
			else if (iteration == 10) {
				invincible = false;
				iteration++;
			}
			radius += dt*speed;
			closeness = (float)radius;
		}
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		currentFrame = ani.getKeyFrame(stateTime, true);
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		warn(sb);
	}
	
	public void reset() {
		explosion.setXY(x, y);
		explosion.setWH((float)radius);
		radius = 0;
		closeness = 0;
		cooldown = true;
		invincible = true;
		iteration = 0;
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }
	
	public void setCoords(float x, float y, int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	public void spawn(double radius) { 
		cooldown = false; 
		if (radius < 5) this.radius = 5;
		else this.radius = radius;
	}
	
	private void move(float dt) {
		switch (direction) {
			case 0:	y += dt*240;
					break;
			
			case 1:	x += dt*240;
					y += dt*240;
					break;
					
			case 2:	x += dt*240;
					break;
			
			case 3:	x += dt*240;
					y -= dt*240;
					break;
					
			case 4:	y -= dt*240;
					break;
			
			case 5:	x -= dt*240;
					y -= dt*240;
					break;
					
			case 6:	x -= dt*240;
					break;
			
			case 7:	x -= dt*240;
					y += dt*240;
					break;
		}
	}
	
	private void warn(SpriteBatch sb) {
		if ((radius >= 40 && radius <= 41)
			|| (radius >= 43 && radius <= 44)
			|| (radius >= 46 && radius <= 47))
			sb.draw(warning, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
}