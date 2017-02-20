package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import java.util.Random;

public class PhasingMissile extends Circle {

	protected Animation ani;
	protected TextureRegion[] missileFrames;
	protected TextureRegion currentFrame;
	protected float stateTime;
	protected Explosion explosion;
	private final float MAX_RADIUS;
	private float timer, cdtimer;
	private boolean cooldown, invincible, fadeIn;
	private Random rand;
	private int hp, phaseTimer, fade;
	private TextureRegion warning;
	
	public PhasingMissile(float radius) {
		hp = 5;
		rand = new Random();
		float[] coords = missileCoords();
		x = coords[0];
		y = coords[1];
		explosion = new Explosion(x, y);
		cooldown = true;
        invincible = true;
        fadeIn = true;
        closeness = 0;
		timer = 0; cdtimer = 10; 
		phaseTimer = 0; fade = 0;
		MAX_RADIUS = radius;
		this.radius = 0;
		
		warning = Ballistic.res.getAtlas("pack").findRegion("warning_invinci"); // TODO: edit
		missileFrames = new TextureRegion[9];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("phaseMissile").split(50, 50);
		int k = 0;
		for (int i = 0; i < 3; i++) 
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
		if (fade < 100 && fadeIn) fade += 4;
		else {
			if (fadeIn) fadeIn = false;
			if (phaseTimer < 60) phaseTimer += 1;
			else {
				fade -= 4;
				if (fade == 0) {
					float tmp[] = missileCoords();
					x = tmp[0]; y = tmp[1];
					explosion.setXY(tmp[0], tmp[1]);
					fadeIn = true;
					phaseTimer = 0;
				}
			}
		}
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
				if (cdtimer > 5f) cdtimer -= dt;
			}
		}
	}
	
	public void render(SpriteBatch sb) {
		explosion.render(sb);
		if (hp == 4 || hp == 3)
			currentFrame = ani.getKeyFrame(stateTime + 0.3f, true);
		else if (hp == 2 || hp == 1)
			currentFrame = ani.getKeyFrame(stateTime + 0.6f, true);
		else
			currentFrame = ani.getKeyFrame(stateTime, true);
		sb.setColor(1, 1, 1, fade/100f);
		sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius/2, (float)radius/2, (float)radius, (float)radius, 1, 1, fade*3.6f);
		sb.setColor(1, 1, 1, 1);
		warn(sb);
	}
	
	public void reset() {
		explosion.setWH((float)radius);
		radius = 0; timer = 0;
		hp = 5; closeness = 0;
        invincible = true;
		cooldown = true;
		fadeIn = true;
		phaseTimer = 0; fade = 0;
		float[] coords = missileCoords();
		x = coords[0]; y = coords[1];
	}
	
	public float getMaxRadius() { return MAX_RADIUS; }
	
	public double getRadius() { return (float)radius; }
	
	public int getHP() { return hp; }
	
	private float[] missileCoords() {
		float temp[] = new float[2];
		if (Ballistic.onMenu) {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 150) + 75;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 450) + 275;
		}
		else {
			temp[0] = rand.nextInt(Ballistic.WIDTH - 150) + 75;
			temp[1] = rand.nextInt(Ballistic.HEIGHT - 250) + 90;
		}
		return temp;
	}
	
	private void warn(SpriteBatch sb) {
		if ((radius >= 140 && radius <= 141)
			|| (radius >= 143 && radius <= 144)
			|| (radius >= 146 && radius <= 147))
			sb.draw(warning, x - (float)radius/2, y - (float)radius/2, (float)radius, (float)radius);
	}
}
