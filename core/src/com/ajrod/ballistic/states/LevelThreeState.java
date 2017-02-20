package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.FragMissile;
import com.ajrod.ballistic.gameobjects.ShieldedMissile;
import com.ajrod.ballistic.gameobjects.SpiralMissile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelThreeState extends LevelState {

	private FragMissile[] fMissile = new FragMissile[2];
	private BasicMissile[] missile = new BasicMissile[2];
	private ShieldedMissile[] sMissile = new ShieldedMissile[2];
	private SpiralMissile spMissile;

	public LevelThreeState(GSM gsm, int score) { 
		super(gsm);
		done = false;
		dead = false;
		this.score = score;
		speed = 45;
		timer = 0;
		
		setMovement = true;
		offset = 0;
		rSteps = 0;
		tex = new Texture(Gdx.files.internal("bg5.png"));
		bg = new TextureRegion(tex);
		
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		for (int i = 0; i < 2; i++) {
			missile[i] = new BasicMissile(80, false);
			fMissile[i] = new FragMissile(80);
			sMissile[i] = new ShieldedMissile(80);
		}
		spMissile = new SpiralMissile(80);
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (setMovement) offset -= dt*5;
			if (dead)
				gameOver(dt);
			else if (!done) {
				if (timer < 16f) { startPhase(dt); }
				else {
					for (int i = 0; i < 2; i++) {
						missile[i].update(dt, speed);
						sMissile[i].update(dt, speed - 20);
						fMissile[i].update(dt, speed/2);
						if (missile[i].getRadius() >= missile[i].getMaxRadius())
							setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
						if (sMissile[i].getRadius() >= sMissile[i].getMaxRadius())
							setDeathParams(sMissile[i].getX(), sMissile[i].getY(), sMissile[i].getMaxRadius()*6);
						if (fMissile[i].getRadius() >= fMissile[i].getMaxRadius())
							setDeathParams(fMissile[i].getX(), fMissile[i].getY(), fMissile[i].getMaxRadius()*6);
						if (fMissile[i].isDead())
							setDeathParams(fMissile[i].getDeathX(), fMissile[i].getDeathY(), 300);
					}
					if (score >= 950) {
						spMissile.update(dt, speed + 30);
						if (spMissile.getRadius() >= spMissile.getMaxRadius())
							setDeathParams(spMissile.getX(), spMissile.getY(), spMissile.getMaxRadius()*6);
					}
				}
				if (score >= 1200) //if (score >= 10) // testing purposes 
				{
					done = true;
					for (int i = 0; i < 2; i++) {
						fMissile[i].kill();
						sMissile[i].reset();
						missile[i].reset(0);
					}
					spMissile.reset();
				}
			}
			else { endPhase(dt); }
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		
		sb.draw(bg, 0, offset, Ballistic.WIDTH, 1600);
		for (int i = 0; i < 2; i++) {
			fMissile[i].render(sb);
			sMissile[i].render(sb);
			missile[i].render(sb);
		}
		if (score >= 950) spMissile.render(sb);
		
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
			
			for (int j = 0; j < 2; j++) {
				if (missile[j].contains(mouse.x, mouse.y)) {
					if (missile[j].onClick(0)) {
						score++;
						if (speed < 55f) speed += 0.25f;
					}
				}
				if (sMissile[j].contains(mouse.x, mouse.y)) {
					if(sMissile[j].getHP() == 1)
						score += 2;
					sMissile[j].onClick();
				}
				if (fMissile[j].miniClicked(mouse.x, mouse.y))
					score++;
				if (fMissile[j].contains(mouse.x, mouse.y)) {
					if (fMissile[j].onClick())
						score++;
				}
			}
			if (score >= 950 && spMissile.contains(mouse.x, mouse.y)) {
				score++;
				spMissile.onClick();
			}
			
			if (pause.contains(mouse.x, mouse.y) && pauses != 0) {
				pauses--;
				running = Running.PAUSED;
			}
			
		}
	}
	
	protected void startPhase(float dt) {
		for (int i = 0; i < 2; i++) {
			if (missile[i].getRadius() >= missile[i].getMaxRadius())
				setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
			if (sMissile[i].getRadius() >= sMissile[i].getMaxRadius())
				setDeathParams(sMissile[i].getX(), sMissile[i].getY(), sMissile[i].getMaxRadius()*6);
			if (fMissile[i].getRadius() >= fMissile[i].getMaxRadius())
				setDeathParams(fMissile[i].getX(), fMissile[i].getY(), fMissile[i].getMaxRadius()*6);
			if (fMissile[i].isDead())
				setDeathParams(fMissile[i].getDeathX(), fMissile[i].getDeathY(), 300);
		}
		if (timer < 1f) {
			missile[0].update(dt, speed);
		}
		else if (timer < 2f) {
			missile[0].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
		}
		else if (timer < 4f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
		}
		else if (timer < 8f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			fMissile[0].update(dt, speed/2);
		}
		else {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
			fMissile[0].update(dt, speed/2);
		}
		timer += dt;
	}

	protected void endPhase(float dt) {
		for (int i = 0; i < 2; i++) {
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
			fMissile[i].update(dt, 0);
		}
		spMissile.update(dt, 0);
		if (timer > 0f) timer -= dt*8;
		else {
			if (setMovement) {
				rSteps = (800f + offset)/120f;
				setMovement = false;
			}
			offset -= rSteps;
			if (offset <= -800)
				gsm.set(new BossThreeState(gsm, score));
		}
			
	}

	protected void gameOver(float dt) {
		for (int i = 0; i < 2; i++) {
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
			fMissile[i].update(dt, 0);
		}
		spMissile.update(dt, 0);
		if (timer > 0f) timer -= dt;
		else gsm.set(new GameOverState(gsm, score));
	}

	protected void setDeathParams(float x, float y, float rad) {
		timer = 2f;
		dead = true;
		deathRad = rad;
		deathX = x;
		deathY = y;
		for (int i = 0; i < 2; i++) {
			missile[i].reset(0);
			sMissile[i].reset();
			fMissile[i].kill();
		}
		spMissile.reset();
		if (Ballistic.soundOn)
			Ballistic.sounds.dead.play();
	}
	
	protected void drawUI(SpriteBatch sb) {
		if (running == Running.RESUMED) {
			pause.render(sb);
			if (score >= 1000)
				Ballistic.font.draw(sb, "" + score, Ballistic.WIDTH - 100, 32);
			else
				Ballistic.font.draw(sb, "" + score, Ballistic.WIDTH - 75, 32);
			Ballistic.font.getData().setScale(0.7f, 0.7f);
			if (pauses == 0)
				Ballistic.font.draw(sb, "X", 40, 35);
			else	
				Ballistic.font.draw(sb, "Pause", 10, 35);
			Ballistic.font.getData().setScale(1, 1);
		}
	}
}