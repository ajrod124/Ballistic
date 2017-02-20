package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.FragMissile;
import com.ajrod.ballistic.gameobjects.PhasingMissile;
import com.ajrod.ballistic.gameobjects.ShieldedMissile;
import com.ajrod.ballistic.gameobjects.SpiralMissile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelFourState extends LevelState {

	private SpiralMissile[] spMissile = new SpiralMissile[2];
	private BasicMissile[] missile = new BasicMissile[2];
	private ShieldedMissile[] sMissile = new ShieldedMissile[2];
	private FragMissile[] fMissile = new FragMissile[2];
	private PhasingMissile[] pMissile = new PhasingMissile[2];
	
	private TextureRegion bg2;
	private float offset2;
	private boolean cycled;

	public LevelFourState(GSM gsm, int score, float offset) { 
		super(gsm);
		done = false;
		dead = false;
		cycled = false;
		this.score = score;
		speed = 45;
		timer = 0;
		
		setMovement = true;
		this.offset = offset; offset2 = 800f;
		rSteps = 0;
		
		tex = new Texture(Gdx.files.internal("bg7-1.png"));
		bg = new TextureRegion(tex);
		
		tex = new Texture(Gdx.files.internal("bg7-2.png"));
		bg2 = new TextureRegion(tex);
		
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		for (int i = 0; i < 2; i++) {
			spMissile[i] = new SpiralMissile(80);
			missile[i] = new BasicMissile(80, false);
			fMissile[i] = new FragMissile(80);
			sMissile[i] = new ShieldedMissile(80);
			pMissile[i] = new PhasingMissile(150);
		}
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (setMovement) {
				if (offset > -1600f) offset -= dt*5;
				if (offset <= -800f || offset2 <= -800f) {
					offset2 -= dt*5;
					if (offset2 <= -800f && !cycled) {
						tex = new Texture(Gdx.files.internal("bg7-3.png"));
						bg = new TextureRegion(tex);
						offset = 800f;
						cycled = true;
					}
					if (offset2 <= -1600f) {
						tex = new Texture(Gdx.files.internal("bg7-4.png"));
						bg2 = new TextureRegion(tex);
						offset2 = 800f;
					}
				}
			}
			if (dead)
				gameOver(dt);
			else if (!done) {
				if (timer < 64f) { startPhase(dt); }
				else {
					for (int i = 0; i < 2; i++) {
						missile[i].update(dt, speed);
						fMissile[i].update(dt, speed/2);
						sMissile[i].update(dt, speed - 20);
						spMissile[i].update(dt, speed + 30);
						if (missile[i].getRadius() >= missile[i].getMaxRadius())
							setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
						if (sMissile[i].getRadius() >= sMissile[i].getMaxRadius())
							setDeathParams(sMissile[i].getX(), sMissile[i].getY(), sMissile[i].getMaxRadius()*6);
						if (fMissile[i].getRadius() >= fMissile[i].getMaxRadius())
							setDeathParams(fMissile[i].getX(), fMissile[i].getY(), fMissile[i].getMaxRadius()*6);
						if (fMissile[i].isDead())
							setDeathParams(fMissile[i].getDeathX(), fMissile[i].getDeathY(), 300);
						if (spMissile[i].getRadius() >= spMissile[i].getMaxRadius())
							setDeathParams(spMissile[i].getX(), spMissile[i].getY(), spMissile[i].getMaxRadius()*6);
					}
					if (score >= 1800) {
						if (part2Timer < 5) part2Timer += dt;
						pMissile[0].update(dt, speed/2);
						if (pMissile[0].getRadius() >= pMissile[0].getMaxRadius())
							setDeathParams(pMissile[0].getX(), pMissile[0].getY(), pMissile[0].getMaxRadius()*6);
						if (part2Timer >= 5) {
							pMissile[1].update(dt, speed/2);
							if (pMissile[1].getRadius() >= pMissile[1].getMaxRadius())
								setDeathParams(pMissile[1].getX(), pMissile[1].getY(), pMissile[1].getMaxRadius()*6);
						}
					}
				}
				/*if (score >= 5000) //if (score >= 10) // testing purposes 
				{
					done = true;
					for (int i = 0; i < 2; i++) {
						fMissile[i].kill();
						sMissile[i].reset();
						missile[i].reset(0);
						spMissile[i].reset();
						pMissile[i].reset();
					}
				}*/
			}
			else { endPhase(dt); }
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		
		if (offset >= -1600f)
			sb.draw(bg, 0, offset, Ballistic.WIDTH, 1600);
		if (offset <= -800f || offset2 <= -800f)
			sb.draw(bg2, 0, offset2, Ballistic.WIDTH, 1600);
		for (int i = 0; i < 2; i++) {
			if (score >= 1800) pMissile[i].render(sb);
			fMissile[i].render(sb);
			sMissile[i].render(sb);
			missile[i].render(sb);
			spMissile[i].render(sb);
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
						speed += 0.01f;
					}
				}
				if (sMissile[i].contains(mouse.x, mouse.y)) {
					if(sMissile[i].getHP() == 1) {
						score += 2;
					}
					sMissile[i].onClick();
				}
				if (fMissile[i].miniClicked(mouse.x, mouse.y))
					score++;
				if (fMissile[i].contains(mouse.x, mouse.y)) {
					if (fMissile[i].onClick()) {
						score++;
					}
				}
				if (spMissile[i].contains(mouse.x, mouse.y)) {
					if (spMissile[i].onClick()) {
						score += 2;
					}
				}
				if (score >= 1800 && pMissile[i].contains(mouse.x, mouse.y)) {
					if(pMissile[i].getHP() == 1)
						score += 5;
					pMissile[i].onClick();
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
			if (fMissile[i].getRadius() >= fMissile[i].getMaxRadius())
				setDeathParams(fMissile[i].getX(), fMissile[i].getY(), fMissile[i].getMaxRadius()*6);
			if (fMissile[i].isDead())
				setDeathParams(fMissile[i].getDeathX(), fMissile[i].getDeathY(), 300);
			if (spMissile[i].getRadius() >= spMissile[i].getMaxRadius())
				setDeathParams(spMissile[i].getX(), spMissile[i].getY(), spMissile[i].getMaxRadius()*6);
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
		else if (timer < 16f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
			fMissile[0].update(dt, speed/2);
		}
		else if (timer < 32f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
			fMissile[0].update(dt, speed/2);
			spMissile[0].update(dt, speed + 30);
		}
		else {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
			fMissile[0].update(dt, speed/2);
			fMissile[1].update(dt, speed/2);
			spMissile[0].update(dt, speed + 30);
		}
		timer += dt;
	}

	protected void endPhase(float dt) {
		for (int i = 0; i < 2; i++) {
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
			fMissile[i].update(dt, 0);
			spMissile[i].update(dt, 0);
			pMissile[i].update(dt, 0);
		}
		timer -= dt;
		if (timer < 0)
			gsm.set(new MenuState(gsm));
	}

	protected void gameOver(float dt) {
		for (int i = 0; i < 2; i++) {
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
			fMissile[i].update(dt, 0);
			spMissile[i].update(dt, 0);
			pMissile[i].update(dt, 0);
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
			spMissile[i].reset();
			pMissile[i].reset();
		}
		if (Ballistic.soundOn)
			Ballistic.sounds.dead.play();
	}
	
	protected void drawUI(SpriteBatch sb) {
		if (running == Running.RESUMED) {
			pause.render(sb);
			Ballistic.font.draw(sb, "" + score, Ballistic.WIDTH - 100, 32);
			Ballistic.font.getData().setScale(0.7f, 0.7f);
			if (pauses == 0)
				Ballistic.font.draw(sb, "X", 40, 35);
			else	
				Ballistic.font.draw(sb, "Pause", 10, 35);
			Ballistic.font.getData().setScale(1, 1);
		}
	}
}
