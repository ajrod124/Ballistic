package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.Explosion;
import com.ajrod.ballistic.gameobjects.FirstBoss;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BossOneState extends LevelState {

	private BasicMissile[] missile = new BasicMissile[5];
	private FirstBoss boss;
	private float deathTimer;
	
	private Explosion[] sequence;
	private Texture exSheet;
	private TextureRegion[] exFrames;
	private float deathRad;
	private int frames;

	public BossOneState(GSM gsm, int score) { 
		super(gsm);
		
		tex = new Texture(Gdx.files.internal("bg2.png"));
		bg = new TextureRegion(tex);
		offset = 0;
		rSteps = 0;
		setMovement = true;
		
		sequence = new Explosion[3];
		exFrames = new TextureRegion[10];
		exSheet = new Texture(Gdx.files.internal("final_explosion.png"));
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		TextureRegion[][] tmp = TextureRegion.split(exSheet, 100, exSheet.getHeight());
		for (int i = 0; i < 10; i++) exFrames[i] = tmp[0][i];
		frames = 0;
		
		deathTimer = 58.00f;
		done = false;
		dead = false;
		this.score = score; timer = 0;
		speed = 40;
		boss = new FirstBoss();
		for (int i = 0; i < 5; i++) {
			missile[i] = new BasicMissile(80, true);
			if (i < 3)
				sequence[i] = new Explosion(0, 0);
		}
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (setMovement) offset -= dt*5;
			if (dead)
				gameOver(dt);
			else if (!done) {
				deathTimer -= dt;
				boss.update(dt, 0.1f);
				if (boss.getRadius() >= boss.getMaxRadius())
					setDeathParams(boss.getX(), boss.getY(), boss.getMaxRadius()*6);
				if (timer < 8f) 
					startPhase(dt);
				else
					for (int i = 0; i < 5; i++) {
						if (i < 3)
							sequence[i].update(dt);
						missile[i].update(dt, speed);
						if (missile[i].getRadius() >= missile[i].getMaxRadius())
							setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
					}
				if (boss.isDefeated() && !dead) {
					score += 100;
					
					timer = 600;
					sequence[0].setXY(Ballistic.WIDTH/2 - deathRad/2, Ballistic.HEIGHT/2 + deathRad/2);
					sequence[1].setXY(Ballistic.WIDTH/2 + deathRad/2, Ballistic.HEIGHT/2);
					sequence[2].setXY(Ballistic.WIDTH/2 - deathRad/2, Ballistic.HEIGHT/2 - deathRad/2);
					
					done = true;
					for (int i = 0; i < 5; i++)
						missile[i].reset(0);
				}
			}
			else { endPhase(dt); }
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		
		sb.draw(bg, 0, offset, Ballistic.WIDTH, 1600);
		for (int i = 0; i < 5; i++) {
			if (i < 3)
				sequence[i].render(sb);
			missile[i].render(sb);
		}
		
		boss.render(sb);
		if(timer <= 540 && timer >= 480) {
			if (frames < 50) 
				sb.draw(exFrames[frames++/5], Ballistic.WIDTH/2 - 50, Ballistic.HEIGHT/2 - 50,
						50, 50, 100, 100, (deathRad*4)/100, (deathRad*4)/100, 90f);
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
			
			if (boss.contains(mouse.x, mouse.y) && !done) {
				deathRad = (float)boss.getRadius();
				boss.onClick();
			}
			
			for (int i = 0; i < 5; i++) {
				if (missile[i].contains(mouse.x, mouse.y)) {
					if (missile[i].onClick((float)boss.getRadius()))
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
		for (int i = 0; i < 5; i++) {
			if (missile[i].getRadius() >= missile[i].getMaxRadius())
				setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
		}
		if (timer < 1f)
			missile[0].update(dt, speed);
		else if (timer < 2f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
		}
		else if (timer < 4f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			missile[2].update(dt, speed);
		}
		else {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			missile[2].update(dt, speed);
			missile[3].update(dt, speed);
		}
		timer += dt;
	}

	protected void endPhase(float dt) {
		boss.update(dt, 0);
		for (int i = 0; i < 5; i++) {
			missile[i].update(dt, 0);
			if (i < 3)
				sequence[i].update(dt);
		}
		timer -= 1;
		if (timer == 585) {
			sequence[0].setWH(deathRad);
			if (Ballistic.soundOn)
				Ballistic.sounds.explosion.play(1f);
		}
		else if (timer == 570) {
			sequence[1].setWH(deathRad);
			if (Ballistic.soundOn)
				Ballistic.sounds.explosion.play(1f);
		}
		else if (timer == 555) {
			sequence[2].setWH(deathRad);
			if (Ballistic.soundOn)
				Ballistic.sounds.explosion.play(1f);
		}
		else if (timer == 540 && Ballistic.soundOn)
			Ballistic.sounds.bigExplosion.play(1f);
		else if (timer == 486 && Ballistic.soundOn)
			Ballistic.sounds.song.play(1f);
		else if (timer <= 120) {
			if (setMovement) {
				rSteps = (800f + offset)/120f;
				setMovement = false;
			}
			offset -= rSteps;
			if (offset <= -800f)
				gsm.set(new LevelTwoState(gsm, score));
		}
	}
	
	protected void gameOver(float dt) {
		boss.update(dt, 0);
		for (int i = 0; i < 5; i++)
			missile[i].update(dt, 0);
		if (timer > 0f) timer -= dt;
		else gsm.set(new GameOverState(gsm, score));
	}
	
	protected void setDeathParams(float x, float y, float rad) {
		timer = 2f;
		dead = true;
		deathRad = rad;
		deathX = x;
		deathY = y;
		boss.reset();
		for (int i = 0; i < 5; i++)
			missile[i].reset(0);
		if (Ballistic.soundOn)
			Ballistic.sounds.dead.play();
	}

	protected void drawUI(SpriteBatch sb) {
		if (running == Running.RESUMED) {
			pause.render(sb);
			if (!done) Ballistic.font.draw(sb, String.format("%.2f", deathTimer), 190, 760);
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
