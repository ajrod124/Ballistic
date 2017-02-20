package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.Explosion;
import com.ajrod.ballistic.gameobjects.ThirdBoss;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BossThreeState extends LevelState {

	private ThirdBoss boss;
	
	private Explosion[] sequence;
	private Texture exSheet;
	private TextureRegion planet1, planet2, planet3, white;
	private float p1offX, p1offY, p1zoom, p2offX, p2offY, p2zoom, p3offX, p3offY, p3zoom;
	private float rStepsY1, rStepsZ, rStepsX2, rStepsY2, rStepsX3, rStepsY3, rads, scalar;
	private float fade, fadeTimer;
	private final float DISTANCE = (float)Math.sqrt(40000.0/3.0);
	private final double P1ANGLE = Math.PI, P2ANGLE = Math.PI/3, P3ANGLE = 5*Math.PI/3;
	private TextureRegion[] exFrames;
	private float deathRad, deathX, deathY;
	private int frames;
	private boolean fadeIn, warped, charging;

	public BossThreeState(GSM gsm, int score) { 
		super(gsm);
		
		tex = new Texture(Gdx.files.internal("white.png"));
		white = new TextureRegion(tex);
		
		tex = new Texture(Gdx.files.internal("bg6.png"));
		bg = new TextureRegion(tex);
		
		tex = new Texture(Gdx.files.internal("greenPlanet.png"));
		planet1 = new TextureRegion(tex);
		
		tex = new Texture(Gdx.files.internal("redPlanet.png"));
		planet2 = new TextureRegion(tex);
		
		tex = new Texture(Gdx.files.internal("bluePlanet.png"));
		planet3 = new TextureRegion(tex);
		
		offset = 0; rads = 0; scalar = 0.05f; fade = 0f; fadeTimer = 0f; fadeIn = true; warped = false; charging = false;
		p1offX = Ballistic.WIDTH/2; p1offY = 1350f; p1zoom = 150f;
		p2offX = Ballistic.WIDTH/4 - 50f; p2offY = 1350f; p2zoom = 100f;
		p3offX = 3*Ballistic.WIDTH/4 + 50f; p3offY = 1350f; p3zoom = 50f;
		rSteps = 0;
		setMovement = true;
		
		sequence = new Explosion[3];
		exFrames = new TextureRegion[10];
		exSheet = new Texture(Gdx.files.internal("final_explosion.png"));
		crack = Ballistic.res.getAtlas("pack").findRegion("cracked_glass");
		TextureRegion[][] tmp = TextureRegion.split(exSheet, 100, exSheet.getHeight());
		for (int i = 0; i < 10; i++) exFrames[i] = tmp[0][i];
		frames = 0;
		deathRad = 50;
		for (int i = 0; i < 3; i++)
			sequence[i] = new Explosion(0, 0);
			
		done = false;
		dead = false;
		this.score = score; timer = 0;
		speed = 40;
		boss = new ThirdBoss();
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (offset >= -800f)
				offset -= dt*5;
			if (setMovement) {
				p1offY -= dt*14;
				p2offY -= dt*12;
				p3offY -= dt*10;
			}
			if (dead)
				gameOver(dt);
			else if (!done) {
				if (timer < 2) startPhase(dt);
				else {
					boss.update(dt);
					for (int i = 0; i < 3; i++)
						sequence[i].update(dt);
					if (boss.killedU()) {
						float tmp[] = boss.getDeathParams();
						setDeathParams(tmp[0], tmp[1], tmp[2]);
					}
				}
				if (boss.isDefeated() && !dead) { 
					score += 400;
					done = true;
					timer = 600;
					sequence[0].setXY(deathX - deathRad/2, deathY + deathRad/2);
					sequence[1].setXY(deathX + deathRad/2, deathY);
					sequence[2].setXY(deathX - deathRad/2, deathY - deathRad/2);
				}
			}
			else { endPhase(dt); }
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		sb.draw(bg, 0, offset, Ballistic.WIDTH, 1600);
		
		if (offset < -200f && fadeTimer < 2f) {
			sb.draw(planet3, p3offX - p3zoom/2, p3offY, p3zoom, p3zoom);
			sb.draw(planet2, p2offX - p2zoom/2, p2offY, p2zoom, p2zoom);
			sb.draw(planet1, p1offX - p1zoom/2, p1offY, p1zoom, p1zoom);
		}
		
		for (int i = 0; i < 3; i++)
			sequence[i].render(sb);
		boss.render(sb);
		if(timer <= 540 && timer >= 480) {
			if (frames < 50) 
				sb.draw(exFrames[frames++/5], deathX - 50, deathY - 50,
						50, 50, 100, 100, (deathRad*4)/100, (deathRad*4)/100, 90f);
		}
		
		if (rads < -40f) {
			sb.setColor(1, 1, 1, fade);
			sb.draw(white, 0, 0, Ballistic.WIDTH, Ballistic.HEIGHT);
			sb.setColor(1, 1, 1, 1);
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
			
			boss.miniClicked(mouse.x, mouse.y);
			if (boss.contains(mouse.x, mouse.y)) {
				deathX = boss.getX();
				deathY = boss.getY();
				boss.onClick();
			}
			
			if (pause.contains(mouse.x, mouse.y) && pauses != 0) {
				pauses--;
				running = Running.PAUSED;
			}
			
		}
	}
	
	protected void startPhase(float dt) {
		boss.startPhase(dt);
		timer += dt;
	}

	protected void endPhase(float dt) {
		boss.update(dt);
		for (int i = 0; i < 3; i++)
			sequence[i].update(dt);
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
			
			if (!charging) {
				if (Ballistic.soundOn)
					Ballistic.sounds.chargingUp.play();
				charging = true;
			}
			
			if (setMovement) {
				rStepsY1 = (p1offY - 300.0f)/120.0f;
				rStepsX2 = ((Ballistic.WIDTH/2 - DISTANCE) - p2offX)/120.0f; 
				rStepsY2 = (p2offY - 500.0f)/120.0f;
				rStepsX3 = (p3offX - (Ballistic.WIDTH/2 + DISTANCE))/120.0f; 
				rStepsY3 = (p3offY - 500.0f)/120.0f;
				rStepsZ = 50.0f/120.0f;
				setMovement = false;
			}
			
			if ((int)p3zoom != 100) {
				p1offY -= rStepsY1;
				p1zoom -= rStepsZ;
				p2offX += rStepsX2;
				p2offY -= rStepsY2;
				p3offX -= rStepsX3;
				p3offY -= rStepsY3;
				p3zoom += rStepsZ;
			}
			
			if (fadeTimer < 2f) {
				p1offX += (136f*scalar)*Math.cos(rads + P1ANGLE);
				p1offY += (136f*scalar)*Math.sin(rads + P1ANGLE);
				p2offX += (136f*scalar)*Math.cos(rads + P2ANGLE);
				p2offY += (136f*scalar)*Math.sin(rads + P2ANGLE);
				p3offX += (136f*scalar)*Math.cos(rads + P3ANGLE);
				p3offY += (136f*scalar)*Math.sin(rads + P3ANGLE);
			}
			
			rads -= scalar;
			if (rads > -40f)
				scalar += dt/8f;
			else {
				if (fadeIn) {
					fade += dt/2;
					if (fade >= 1) {
						fade = 1;
						fadeIn = false;
					}
				}
				else {
					if (!warped) {
						if (Ballistic.soundOn)
							Ballistic.sounds.warped.play();
						tex = new Texture(Gdx.files.internal("bg7-1.png"));
						bg = new TextureRegion(tex);
						offset = 0;
						warped = true;
					}
					if (fadeTimer < 2f) fadeTimer += dt;
					else {
						if (fade > 0) fade -= dt/4;
					}
				}
			}
			if (rads <= -350f)
				gsm.set(new LevelFourState(gsm, score, offset));
		}
	}

	protected void gameOver(float dt) {
		boss.update(dt);
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
