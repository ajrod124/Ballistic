package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.ShieldedMissile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelOneState extends LevelState {

	private BasicMissile[] missile = new BasicMissile[3];
	private ShieldedMissile[] sMissile = new ShieldedMissile[2];

	public LevelOneState(GSM gsm) { 
		super(gsm);
		offset = 0;
		rSteps = 0;
		done = false;
		dead = false;
		setMovement = true;
		tex = new Texture(Gdx.files.internal("bg1.png"));
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		bg = new TextureRegion(tex);
		score = 0;
		speed = 30;
		timer = part2Timer = 0;
		for (int i = 0; i < 3; i++) {
			missile[i] = new BasicMissile(80, false);
			if (i < 2)
				sMissile[i] = new ShieldedMissile(80);
		}

		Ballistic.ar.showOrLoadInterstitial();
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (setMovement) {
				if (offset > -150)
					offset -= 3;
				else
					offset -= dt*5;
			}
			if (dead)
				gameOver(dt);
			else if (!done) {
				if (timer < 2f) { startPhase(dt); }
				else {
					for (int i = 0; i < 3; i++) {
						missile[i].update(dt, speed);
						if (missile[i].getRadius() >= missile[i].getMaxRadius())
							setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
					}
					if (score >= 50) {
						if (part2Timer < 2) part2Timer += dt;
						sMissile[0].update(dt, speed - 20);
						if (sMissile[0].getRadius() >= sMissile[0].getMaxRadius())
							setDeathParams(sMissile[0].getX(), sMissile[0].getY(), sMissile[0].getMaxRadius()*6);
						if (part2Timer >= 2) {
							sMissile[1].update(dt, speed - 20);
							if (sMissile[1].getRadius() >= sMissile[1].getMaxRadius())
								setDeathParams(sMissile[1].getX(), sMissile[1].getY(), sMissile[1].getMaxRadius()*6);
						}
					}
				}
				if (score >= 150)
				{
					done = true;
					for (int i = 0; i < 3; i++) {
						missile[i].reset(0);
						if (i < 2)
							sMissile[i].reset();
					}
				}
			}
			else { endPhase(dt); }
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		
		sb.draw(bg, 0, offset, Ballistic.WIDTH, 1600);
		for (int i = 0; i < 3; i++) {
			if (i < 2 && score >= 50)
				sMissile[i].render(sb);
			missile[i].render(sb);
		}
		
		if (dead) {
			float fade = timer;
			if (timer > 1f) fade = 1;
			sb.setColor(1,1,1,1);
			sb.draw(crack, deathX - (float)deathRad/2, deathY - (float)deathRad/2, (float)deathRad, (float)deathRad);
			sb.setColor(1, 1, 1, fade);
		}
		else {
			renderAndHandleInputPaused(sb);
			drawUI(sb);
		}
		
		sb.end();
	}

	public void handleInput() {
		if(Gdx.input.justTouched()) {
			if (Ballistic.soundOn)
				Ballistic.sounds.shot.play(0.3f);
			mouse.x = Gdx.input.getX();
			mouse.y = Gdx.input.getY();
			cam.unproject(mouse);
			
			for (int i = 0; i < 3; i++) {
				if (missile[i].contains(mouse.x, mouse.y)) {
					if (missile[i].onClick(0)) {
						score++;
						if (speed < 40f) speed += 0.25f;
					}
				}
				if (i < 2 && score >= 50) {
					if (sMissile[i].contains(mouse.x, mouse.y)) {
						if(sMissile[i].getHP() == 1)
							score += 2;
						sMissile[i].onClick();
					}
				}
			}
			
			if (pause.contains(mouse.x, mouse.y) && pauses != 0) {
				pauses--;
				running = Running.PAUSED;
			}
			
		}
	}
	
	protected void startPhase(float dt) {
		for (int i = 0; i < 3; i++) {
			if (missile[i].getRadius() >= missile[i].getMaxRadius())
				setDeathParams(missile[i].getX(), missile[i].getY(), (float)missile[i].getRadius());
		}
		if (timer < 1f)
			missile[0].update(dt, speed);
		else {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
		}
		timer += dt;
	}

	protected void endPhase(float dt) {
		for (int i = 0; i < 3; i++) {
			missile[i].update(dt, 0);
			if (i < 2)
				sMissile[i].update(dt, 0);
		}
		if (timer > 0f) timer -= dt;
		else {
			if (setMovement) {
				rSteps = (800f + offset)/120f;
				setMovement = false;
			}
			offset -= rSteps;
			if (offset <= -800)
				gsm.set(new BossOneState(gsm, score));
		}
	}
	
	protected void gameOver(float dt) {
		for (int i = 0; i < 3; i++) {
			missile[i].update(dt, 0);
			if (i < 2)
				sMissile[i].update(dt, 0);
		}
		if (timer > 0f) timer -= dt;
		else gsm.set(new GameOverState(gsm, score));
	}
	
	protected void setDeathParams(float x, float y, float rad) {
		timer = 2f;
		dead = true;
		deathRad = rad;
		deathX = x;
		deathY = y;
		for (int i = 0; i < 3; i++) {
			missile[i].reset(0);
			if (i < 2)
				sMissile[i].reset();
		}
		if (Ballistic.soundOn)
			Ballistic.sounds.dead.play();
	}
	
	protected void drawUI(SpriteBatch sb) {
		if (running == Running.RESUMED) {
			pause.render(sb);
			if (score >= 100)
				Ballistic.font.draw(sb, "" + score, Ballistic.WIDTH - 75, 32);
			else if (score >= 10)
				Ballistic.font.draw(sb, "" + score, Ballistic.WIDTH - 50, 32);
			else
				Ballistic.font.draw(sb, "" + score, Ballistic.WIDTH - 25, 32);
			Ballistic.font.getData().setScale(0.7f, 0.7f);
			if (pauses == 0)
				Ballistic.font.draw(sb, "X", 40, 35);
			else	
				Ballistic.font.draw(sb, "Pause", 10, 35);
			Ballistic.font.getData().setScale(1, 1);
		}
	}
}
