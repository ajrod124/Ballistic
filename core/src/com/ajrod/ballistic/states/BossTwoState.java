package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.Explosion;
import com.ajrod.ballistic.gameobjects.SecondBoss;
import com.ajrod.ballistic.gameobjects.ShieldedMissile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BossTwoState extends LevelState {

	private BasicMissile[] missile = new BasicMissile[3];
	private ShieldedMissile[] sMissile = new ShieldedMissile[3];
	private SecondBoss boss;
	private float deathTimer;
	
	private Explosion[] sequence;
	private Texture exSheet;
	private TextureRegion mars, bgTrans;
	private TextureRegion[] exFrames;
	private float deathRad, deathX, deathY, rSteps2, rSteps3, rSteps4, rSteps5, zoom, xOffset, yOffset, fade;
	private int frames;

	public BossTwoState(GSM gsm, int score) { 
		super(gsm);
		
		tex = new Texture(Gdx.files.internal("bg4.png"));
		bg = new TextureRegion(tex);
		offset = fade = 0f;
		zoom = 100f;
		xOffset = 50f;
		yOffset = 900f;
		setMovement = true;
		
		tex = new Texture(Gdx.files.internal("mars.png"));
		mars = new TextureRegion(tex);
		
		tex = new Texture(Gdx.files.internal("bg4transition.png"));
		bgTrans = new TextureRegion(tex);
		
		sequence = new Explosion[3];
		exFrames = new TextureRegion[10];
		exSheet = new Texture(Gdx.files.internal("final_explosion.png"));
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		TextureRegion[][] tmp = TextureRegion.split(exSheet, 100, exSheet.getHeight());
		for (int i = 0; i < 10; i++) exFrames[i] = tmp[0][i];
		frames = 0;
		
		deathTimer = 54.0f;
		done = false;
		dead = false;
		this.score = score;
		speed = 45;
		timer = 0;
		boss = new SecondBoss();
		for (int i = 0; i < 3; i++) {
			sequence[i] = new Explosion(0, 0);
			missile[i] = new BasicMissile(80, false);
			sMissile[i] = new ShieldedMissile(80);
		}
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (setMovement) {
				offset -= dt*5;
				yOffset -= dt*10;
			}
			if (dead)
				gameOver(dt);
			else if (!done) {
				deathTimer -= dt;
				boss.update(dt, 10);
				if (boss.killedU()) 
					setDeathParams(boss.getX(), boss.getY(), 340*6);
				if (timer < 16f) { startPhase(dt); }
				else {
					for (int i = 0; i < 3; i++) {
						sequence[i].update(dt);
						sMissile[i].update(dt, speed - 20);
						missile[i].update(dt, speed);
						if (missile[i].getRadius() >= missile[i].getMaxRadius())
							setDeathParams(missile[i].getX(), missile[i].getY(), missile[i].getMaxRadius()*6);
						if (sMissile[i].getRadius() >= sMissile[i].getMaxRadius())
							setDeathParams(sMissile[i].getX(), sMissile[i].getY(), sMissile[i].getMaxRadius()*6);
					}
				}
				if (boss.isDefeated() && !dead) {
					score += 200;
					
					timer = 600;
					sequence[0].setXY(deathX - deathRad/2, deathY + deathRad/2);
					sequence[1].setXY(deathX + deathRad/2, deathY);
					sequence[2].setXY(deathX - deathRad/2, deathY - deathRad/2);
					
					done = true;
					for (int i = 0; i < 3; i++) {
						missile[i].reset(0);
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
		
		if (done) {
			sb.setColor(1, 1, 1, fade);
			sb.draw(bgTrans, 0, 0, Ballistic.WIDTH, Ballistic.HEIGHT);
			sb.setColor(1, 1, 1, 1);
		}
		
		sb.draw(mars, xOffset, yOffset, zoom, zoom);
		
		for (int i = 0; i < 3; i++) {
			sequence[i].render(sb);
			sMissile[i].render(sb);
			missile[i].render(sb);
		}
		
		boss.render(sb);
		if(timer <= 540 && timer >= 480) {
			if (frames < 50) 
				sb.draw(exFrames[frames++/5], deathX - 50, deathY - 50,
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
				deathX = boss.getX();
				deathY = boss.getY();
				boss.onClick();
			}
			
			for (int j = 0; j < 3; j++) {
				if (missile[j].contains(mouse.x, mouse.y)) {
					if (missile[j].onClick(0))
						score++;
				}
				if (sMissile[j].contains(mouse.x, mouse.y)) {
					if(sMissile[j].getHP() == 1)
						score += 2;
					sMissile[j].onClick();
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
		else if (timer < 8f) {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
		}
		else {
			missile[0].update(dt, speed);
			missile[1].update(dt, speed);
			missile[2].update(dt, speed);
			sMissile[0].update(dt, speed - 20);
			sMissile[1].update(dt, speed - 20);
		}
		timer += dt;
	}

	protected void endPhase(float dt) { 
		boss.update(dt, 0);
		for (int i = 0; i < 3; i++) {
			sequence[i].update(dt);
			missile[i].update(dt, 0);
			sMissile[i].update(dt, 0);
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
		else if (timer == 555 && Ballistic.soundOn) {
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
				rSteps2 = 610.0f/120.0f;
				rSteps3 = 1500.0f/120.0f;
				rSteps4 = 1.0f/120.0f;
				rSteps5 = (1486f + yOffset)/120f;
				setMovement = false;
			}
			offset -= rSteps;
			xOffset -= rSteps2;
			yOffset -= rSteps5;
			zoom += rSteps3;
			fade += rSteps4;
			if (offset <= -800)
				gsm.set(new LevelThreeState(gsm, score));
		}
	}

	protected void gameOver(float dt) {
		boss.update(dt, 0);
		for (int i = 0; i < 3; i++) {
			missile[i].update(dt, 0);
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
		boss.reset();
		for (int i = 0; i < 3; i++) {
			missile[i].reset(0);
			sMissile[i].reset();
		}
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
