package com.ajrod.ballistic.gameobjects;

import java.util.LinkedList;
import java.util.Queue;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class ThirdBossMinions extends Circle {
	
	private Queue<Coords> q;
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion[] breakout;
	private TextureRegion shelled;
	private TextureRegion currentFrame;
	private final float MAX_RADIUS;
	private boolean invincible;
	private float stateTime;
	private int breakCounter;
	private Explosion explosion;
	private TextureRegion warning;
	
	public ThirdBossMinions(float radius, float x, float y) {
		this.x = x;
		this.y = y;
		breakCounter = 0;
		explosion = new Explosion(x, y);
		q = new LinkedList<Coords>();
        invincible = true;
		MAX_RADIUS = radius;
		this.radius = 30;
		closeness = 0;
		
		missileFrames = new TextureRegion[3];
		breakout = new TextureRegion[3];
		shelled = Ballistic.res.getAtlas("pack").findRegion("shell");
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_invinci");
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("invinciMissile").split(25, 25);
		for (int i = 0; i < 3; i++) missileFrames[i] = tmp[0][i];
		TextureRegion[][] tmp2 = Ballistic.res.getAtlas("pack").findRegion("breakout").split(25, 25);
		for (int i = 0; i < 3; i++) breakout[i] = tmp2[0][i];
		
		ani = new Animation(0.1f, missileFrames);
		ani.setPlayMode(PlayMode.LOOP_PINGPONG);
		stateTime = 0f;
	}
	
	public boolean onClick() {
        if (!invincible) {
        	if (Ballistic.soundOn)
        		Ballistic.sounds.hit.play(1.0f);
        	radius -= 20;
        	closeness = (float)radius/1.6f;
        	if (radius < 1) radius = 1;
        	return true;
        }
        return false;
    }
	
	public Coords update(float dt, float speed, float x, float y) {
		explosion.update(dt);
		if (radius > 0) {
			Coords temp;
			stateTime += dt;
			if (stateTime > 0.29f) stateTime = 0;
			if (invincible) temp = follow(x, y);
			else {
				radius += dt*speed;
				closeness = (float)radius/1.6f;
				return null;
			}
			return temp;
		}
		return null;
	}
	
	public Coords follow(float x, float y) {
		if (q.size() < 10) {
			q.add(new Coords(x, y));
			return null;
		}
		else if (q.size() == 10) {
			q.add(new Coords(x, y));
			Coords temp = q.remove();
			this.x = temp.getX();
			this.y = temp.getY();
			return temp;
		}
		else return null;
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		currentFrame = ani.getKeyFrame(stateTime, true);
		if (!invincible) {
			if (breakCounter <= 14) {
				sb.draw(breakout[breakCounter++/5], x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
			}
			else
				sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		}
		else
			sb.draw(shelled, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		warn(sb);
	}
	
	public void reset() {
		explosion.setXY(x, y);
		explosion.setWH((float)radius);
		radius = 0;
		closeness = (float)radius/1.6f;
		invincible = true;
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	public double getRadius() { return (float)radius; }
	
	public void disengage() { invincible = false; }
	public boolean isInvincible() { return invincible; }
	
	public class Coords {
		private float x;
		private float y;
		public Coords(float x, float y) {
			this.x = x;
			this.y = y;
		}
		public float getX() { return x; }
		public float getY() { return y; }
	}
	
	private void warn(SpriteBatch sb) {
		if ((radius >= 70 && radius <= 71)
			|| (radius >= 73 && radius <= 74)
			|| (radius >= 76 && radius <= 77))
			sb.draw(warning, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
}