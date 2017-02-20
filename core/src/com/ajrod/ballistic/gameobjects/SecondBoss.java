package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SecondBoss extends Box {
	
	private SecondBossP2 p2;
	private Animation ani;
	private TextureRegion health;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame, healthBar, barEnd;
	private int hp;
	private boolean defeated, goingRight, invincible, toggle;
	private float stateTime;
	private Explosion explosion;
	private boolean goingDown, hit;
	private int cycles;
	private float closeness;
	
	public SecondBoss() {
		p2 = new SecondBossP2(hp);
		defeated = false;
		hit = false;
		invincible = false;
		closeness = 19;
		x = -50;
		y = Ballistic.HEIGHT - 100;
		explosion = new Explosion(x, y);
		goingRight = true;
		goingDown = true;
		toggle = true;
		cycles = 1;
		hp = 100;
		//hp = 5; // testing purposes
		width = 150;
		height = 100;
		
		healthBar = Ballistic.res.getAtlas("pack").findRegion("HealthBar");
		barEnd = Ballistic.res.getAtlas("pack").findRegion("HealthEnd");
		health = Ballistic.res.getAtlas("pack").findRegion("Health");
		
		missileFrames = new TextureRegion[12];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("sharkMissile_v2").split(75, 50);
		int k = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 3; j++) {
				missileFrames[k] = tmp[i][j];
				k++;
			}
			
		ani = new Animation(0.1f, missileFrames);
		ani.setPlayMode(PlayMode.LOOP_PINGPONG);
		stateTime = 0f;
	}
	
	public void onClick() {
		if (cycles == 3) {
			p2.onClick();
			if (p2.isDefeated())
				reset();
		}
		else {
			if (!invincible) {
				if (Ballistic.soundOn)
					Ballistic.sounds.hit.play(1.0f);
				hp--;
			}
			hit = true;
			if (hp == 0) { 
				if (Ballistic.soundOn)
					Ballistic.sounds.explosion.play(1.0f);
				reset(); 
			}
		}
	}
	
	public void update(float dt, float increase) {
		explosion.update(dt);
		stateTime += dt;
		if (stateTime > 0.29f) stateTime = 0;
		if (cycles == 3) {
			if (toggle) {
				p2.setHP(hp);
				invincible = true;
				width = 0;
				toggle = false;
			}
			if (!defeated) {
				p2.update(dt, 1);
			}
			else {
				p2.update(dt, 0);
			}
		}
		else {
			if (goingRight) {
				x += dt*240;
				if (x >= Ballistic.WIDTH + 50) {
					goingRight = false;
					if (goingDown) {
						y -= 100;
						if (y < 250) {
							goingDown = false;
						}
					}
					else {
						y += 100;
						if (y > 650) {
							goingDown = true;
							cycles++;
						}
					}
				}
			}
			else {
				x -= dt*240;
				if (x <= -50) {
					goingRight = true;
					if (goingDown) {
						y -= 100;
						if (y < 150) {
							goingDown = false;
						}
					}
					else {
						y += 100;
						if (y > 650) {
							goingDown = true;
							cycles++;
						}
					}
				}
			}
		}
	}
	
	public void render(SpriteBatch sb) {
		if (cycles == 3) p2.render(sb);
		else {
			explosion.render(sb);
			if (!hit) {
				if (goingRight) currentFrame = ani.getKeyFrame(stateTime, true);
				else currentFrame = ani.getKeyFrame(stateTime + 0.6f, true);
			}
			else {
				if (goingRight) {
					currentFrame = ani.getKeyFrame(stateTime + 0.3f, true);
					hit = false;
				}
				else {
					currentFrame = ani.getKeyFrame(stateTime + 0.9f, true);
					hit = false;
				}
			}
			sb.draw(currentFrame, x - width/2, y - height/2, width, height);
			
			if (hp > 0) {
				sb.draw(healthBar, 32, Ballistic.HEIGHT - 29, 416, 20);
				sb.draw(barEnd, 40 + hp*4, Ballistic.HEIGHT - 25, 4, 12);
				sb.draw(health, 40, Ballistic.HEIGHT - 25, hp*4, 12);
			}
		}
	}
	
	public void reset() { 
		if (cycles == 3) {
			p2.reset();
			defeated = true;
		}
		else {
			explosion.setXY(x, y);
			explosion.setWH(width);
			width = 0; 
			height = 0;
			defeated = true;
		}
	}
	
	public boolean killedU() { return p2.getRadius() >= p2.getMaxRadius(); }
	
	public boolean isDefeated() { return defeated || p2.isDefeated(); }
	
	@Override
	public boolean contains(float x, float y) {
		if (cycles == 3) return p2.contains(x, y);
		return x > this.x - width/2 &&
				x < this.x + width/2 &&
				y > this.y - height/2 &&
				y < this.y + height/2;
	}
	
	public float getCloseness() { return closeness; }
	
	public double getRadius() {
		if (cycles == 3) return p2.getRadius();
		else return 75;
	}
	
	public float getX() { 
		if (cycles == 3) return Ballistic.WIDTH/2;
		else return x; 
	}
	public float getY() { 
		if (cycles == 3) return Ballistic.HEIGHT/2;
		else return y; 
	}

}