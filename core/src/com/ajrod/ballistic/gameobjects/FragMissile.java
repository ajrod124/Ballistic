package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class FragMissile extends Circle {
	
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame;
	private final float MAX_RADIUS;
	private float timer, cdtimer, deathX, deathY;
	private boolean cooldown, invincible;
	private Random rand;
	private float stateTime;
	private Explosion explosion;
	private MiniMissile[] mini;
	private TextureRegion warning;
	
	public FragMissile(float radius) {
		rand = new Random();
		float[] coords = missileCoords();
		x = coords[0];
		y = coords[1];
		explosion = new Explosion(x, y);
		mini = new MiniMissile[3];
		initialPattern();
		closeness = 0;
		cooldown = true;
        invincible = true;
		timer = 0; cdtimer = 3;
		MAX_RADIUS = radius;
		this.radius = 0;
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_basic");
		missileFrames = new TextureRegion[3];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("fragMissile").split(25, 25);
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
			radius += dt*speed;
			closeness = (float)radius/1.6f;
		}
		else {
			if (!isReady()) {
				for (int i = 0; i < 3; i++) {
					if (speed > 20) mini[i].update(dt, speed - 20);
					else mini[i].update(dt, 1);
				}
			}
			else {
				for (int i = 0; i < 3; i++) {
					mini[i].update(dt, 0);
				}
				timer += dt;
				if (timer > cdtimer + rand.nextFloat() && isReady()) {
					explosion.setXY(x, y);
					getNewPattern(rand.nextInt(4));
					cooldown = false;
	                invincible = false;
					if (cdtimer > 1f) cdtimer -= dt;
				}
			}
		}
	}

	public void render(SpriteBatch sb) {
		explosion.render(sb);
		for (int i = 0; i < 3; i++)
			mini[i].render(sb);
		currentFrame = ani.getKeyFrame(stateTime, true);
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		warn(sb);
	}
	
	public void reset() {
		explosion.setWH((float)radius);
		for (int i = 0; i < 3; i++) {
			if (radius > 40) mini[i].spawn(radius - 40);
			else mini[i].spawn(1);
		}
		radius = 0; timer = 0;
		closeness = 0;
        invincible = true;
		cooldown = true;
		float[] coords = missileCoords();
		x = coords[0]; y = coords[1];
	}
	
	public void kill() {
		explosion.setWH((float)radius);
		radius = 0; timer = 0;
		closeness = 0;
        invincible = true;
		cooldown = true;
		for (int i = 0; i < 3; i++) {
			mini[i].reset();
		}
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }
	
	private float[] missileCoords() {
		float temp[] = new float[2];
		if (Ballistic.onMenu) {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 120) + 60;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 320) + 260;
		}
		else {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 120) + 60;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 220) + 110;
		}
		return temp;
	}
	
	private void getNewPattern(int pattern) {
		switch (pattern) {
			case 0: mini[0].setCoords(x, y, 0);
					mini[1].setCoords(x, y, 3);
					mini[2].setCoords(x, y, 5);
					break;
					
			case 1: mini[0].setCoords(x, y, 1);
					mini[1].setCoords(x, y, 3);
					mini[2].setCoords(x, y, 6);
					break;
				
			case 2: mini[0].setCoords(x, y, 2);
					mini[1].setCoords(x, y, 5);
					mini[2].setCoords(x, y, 7);
					break;
				
			case 3: mini[0].setCoords(x, y, 1);
					mini[1].setCoords(x, y, 4);
					mini[2].setCoords(x, y, 7);
					break;
		}
	}
	
	private void initialPattern() {
			mini[0] = new MiniMissile(50, 0, 0, 0);
			mini[1] = new MiniMissile(50, 0, 0, 0);
			mini[2] = new MiniMissile(50, 0, 0, 0);
	}
	
	private boolean isReady() {
		return mini[0].getRadius() == 0
				&& mini[1].getRadius() == 0
				&& mini[2].getRadius() == 0;
	}
	
	public boolean isDead() {
		for (int i = 0; i < 3; i++) {
			if (mini[i].getRadius() >= mini[i].getMaxRadius()) {
				deathX = mini[i].getX();
				deathY = mini[i].getY();
				return true;
			}
		}
		return false;
	}
	
	public float getDeathX() { return deathX; }
	public float getDeathY() { return deathY; }
	
	public boolean miniClicked(float x, float y) {
		for (int i = 0; i < 3; i++) {
			if (mini[i].contains(x, y)) {
				return mini[i].onClick();
			}
		}
		return false;
	}
	
	private void warn(SpriteBatch sb) {
		if ((radius >= 70 && radius <= 71)
			|| (radius >= 73 && radius <= 74)
			|| (radius >= 76 && radius <= 77))
			sb.draw(warning, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
	
	public void menuKill() {
		mini[0].onClick();
		mini[1].onClick();
		mini[2].onClick();
	}
}