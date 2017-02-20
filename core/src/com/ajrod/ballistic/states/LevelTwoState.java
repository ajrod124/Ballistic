package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.FragMissile;
import com.ajrod.ballistic.gameobjects.ShieldedMissile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelTwoState extends LevelState {

	private BasicMissile[] missile = new BasicMissile[2];
	private ShieldedMissile[] sMissile = new ShieldedMissile[2];
	private FragMissile[] fMissile = new FragMissile[2];

	public LevelTwoState(GSM gsm, int score) { 
		super(gsm);
		done = false;
		dead = false;
		this.score = score;
		speed = 35;
		timer = part2Timer = 0;
		
		setMovement = true;
		offset = 0;
		rSteps = 0;
		tex = new Texture(Gdx.files.internal("bg3.png"));
		bg = new TextureRegion(tex);
		
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		for (int i = 0; i < 2; i++) {
			missile[i] = new BasicMissile(80, false);
			sMissile[i] = new ShieldedMissile(80);
			fMissile[i] = new FragMissile(80);
		}
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (setMovement) offset -= dt*5;
			if (dead)
				gameOver(dt);
			else if (!done) {
				if (timer < 8f) { startPhase(dt); }
				else {
					for (int i = 0; i < 2; i++) {
						sMissile[i].update(dt, speed - 20);
						if (sMissile[i].getRadius() >= sMissile[i].getMaxRadius())
							setDeathParams(sMissile[i].getX(), sMissile[i].getY(), sMissile[i].getMaxRadius()*6);
						missile[i].update(dt, speed);
						if (missile[i].getRadius() >= missile[i].getMaxRadius())
							setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
					}
					if (score >= 400) {
						if (part2Timer < 2) part2Timer += dt;
						fMissile[0].update(dt, speed/2);
						if (fMissile[0].getRadius() >= fMissile[0].getMaxRadius())
							setDeathParams(fMissile[0].getX(), fMissile[0].getY(), fMissile[0].getMaxRadius()*6);
						if (fMissile[0].isDead())
							setDeathParams(fMissile[0].getDeathX(), fMissile[0].getDeathY(), 300);
						if (part2Timer >= 2) {
							fMissile[1].update(dt, speed/2);
							if (fMissile[1].getRadius() >= fMissile[1].getMaxRadius())
								setDeathParams(fMissile[1].getX(), fMissile[1].getY(), fMissile[1].getMaxRadius()*6);
							if (fMissile[1].isDead())
								setDeathParams(fMissile[1].getDeathX(), fMissile[1].getDeathY(), 300);
						}
					}
				}
				if (score >= 550) //if (score >= 130) // testing purposes 
				{
					done = true;
					for (int i = 0; i < 2; i++) {
						missile[i].reset(0);
						sMissile[i].reset();
						fMissile[i].kill();
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
		for (int i = 0; i < 2; i++) {
			if (score >= 400) fMissile[i].render(sb);
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
			
			for (int i = 0; i < 2; i++) {
				if (missile[i].contains(mouse.x, mouse.y)) {
					if (missile[i].onClick(0)) {
						score++;
						if (speed < 45f) speed += 0.25f;
					}
				}
				if (sMissile[i].contains(mouse.x, mouse.y)) {
					if(sMissile[i].getHP() == 1)
						score += 2;
					sMissile[i].onClick();
				}
				if (fMissile[i].miniClicked(mouse.x, mouse.y))
					score++;
				if (fMissile[i].contains(mouse.x, mouse.y)) {
					if (fMissile[i].onClick())
						score++;
				}
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
		else {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
		}
		timer += dt;
	}
	
	protected void endPhase(float dt) {
		for (int i = 0; i < 2; i++) {
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
			fMissile[i].update(dt, 0);
		}
		if (timer > 0f) timer -= dt*4;
		else {
			if (setMovement) {
				rSteps = (800f + offset)/120f;
				setMovement = false;
			}
			offset -= rSteps;
			if (offset <= -800)
				gsm.set(new BossTwoState(gsm, score));
		}
	}
	
	protected void gameOver(float dt) {
		for (int i = 0; i < 2; i++) {
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
			fMissile[i].update(dt, 0);
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
		for (int i = 0; i < 2; i++) {
			missile[i].reset(0);
			sMissile[i].reset();
			fMissile[i].kill();
		}
		if (Ballistic.soundOn)
			Ballistic.sounds.dead.play();
	}
	
	protected void drawUI(SpriteBatch sb) {
		if (running == Running.RESUMED) {
			pause.render(sb);
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
