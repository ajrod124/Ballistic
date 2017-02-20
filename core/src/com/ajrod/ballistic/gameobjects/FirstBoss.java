package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class FirstBoss extends Circle {
	
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion health;
	private TextureRegion currentFrame, healthBar, barEnd;
	private final float MAX_RADIUS;
	private int hp;
	private boolean defeated, hit;
	private float stateTime;
	private Explosion explosion;
	
	public FirstBoss() {
		defeated = false;
		hit = false;
		x = Ballistic.WIDTH/2;
		y = Ballistic.HEIGHT/2;
		explosion = new Explosion(x, y);
		hp = 100;
		//hp = 20; // testing purposes
		MAX_RADIUS = 348;
		this.radius = 0;
		closeness = 0;
		
		healthBar = Ballistic.res.getAtlas("pack").findRegion("HealthBar");
		barEnd = Ballistic.res.getAtlas("pack").findRegion("HealthEnd");
		health = Ballistic.res.getAtlas("pack").findRegion("Health");
		
		missileFrames = new TextureRegion[6];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("boss1").split(100, 100);
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
		if (Ballistic.soundOn)
			Ballistic.sounds.hit.play(1.0f);
		hp--;
		hit = true;
		if (hp == 0) {
			if (Ballistic.soundOn)
				Ballistic.sounds.explosion.play(1.0f);
			reset();
		}
	}
	
	public void update(float dt, float speed) {
		explosion.update(dt);
		stateTime += dt;
		if (stateTime > 0.29f) stateTime = 0;
		if(radius < MAX_RADIUS) {
			radius += speed;
			closeness = (float)radius/6.96f;
		}
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		if (!hit) currentFrame = ani.getKeyFrame(stateTime, true);
		else {
			currentFrame = ani.getKeyFrame(stateTime + 0.3f, true);
			hit = false;
		}
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
		if (hp > 0) {
			sb.draw(healthBar, 32, Ballistic.HEIGHT - 29, 416, 20);
			sb.draw(barEnd, 40 + hp*4, Ballistic.HEIGHT - 25, 4, 12);
			sb.draw(health, 40, Ballistic.HEIGHT - 25, hp*4, 12);
		}
	}
	
	public void reset() { 
		explosion.setWH((float)radius);
		radius = 0; 
		closeness = 0;
		defeated = true;
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }
	
	public boolean isDefeated() { return defeated; }

}