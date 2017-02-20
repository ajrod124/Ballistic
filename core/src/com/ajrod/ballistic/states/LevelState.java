package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.Button;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class LevelState extends State{

	protected final int MAX_FINGERS = 3;
	protected float timer, part2Timer, speed;
	protected int score;
	protected boolean done, dead, setMovement;
	protected TextureRegion bg;
	protected Texture tex;
	protected float deathRad, deathX, deathY;
	protected float offset;
	protected float rSteps;
	protected TextureRegion crack;
	protected enum Running {
		PAUSED, RESUMED
	}
	protected int pauses;
	protected Running running;
	private Button resume;
	protected Button pause;
	
	protected LevelState(GSM gsm) {
		super(gsm);
		pauses = 3;
		running = Running.RESUMED;
		resume = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2 - 50, 153, 78);
		pause = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"), 50, 25, 100, 50);
	}
	
	public abstract void update(float dt);
	public abstract void render(SpriteBatch sb);
	public abstract void handleInput();
	protected abstract void startPhase(float dt);
	protected abstract void endPhase(float dt);
	protected abstract void gameOver(float dt);
	protected abstract void setDeathParams(float x, float y, float rad);
	protected abstract void drawUI(SpriteBatch sb);
	
	protected void renderAndHandleInputPaused(SpriteBatch sb) {
		if (running == Running.PAUSED) {
			sb.setColor(1f, 1f, 1f, 1f);
			resume.render(sb);
			Ballistic.font.getData().setScale(2, 2);
			Ballistic.font.draw(sb, "PAUSED", 100, Ballistic.HEIGHT/2 + 100);
			Ballistic.font.getData().setScale(0.9f, 0.9f);
			Ballistic.font.draw(sb, "Resume", 172f, Ballistic.HEIGHT/2 - 35);
			Ballistic.font.getData().setScale(1, 1);
			sb.setColor(1f, 1f, 1f, 0.5f);
			if(Gdx.input.justTouched()) {
				mouse.x = Gdx.input.getX();
				mouse.y = Gdx.input.getY();
				cam.unproject(mouse);
				if (resume.contains(mouse.x, mouse.y)) {
					running = Running.RESUMED;
					sb.setColor(1, 1, 1, 1);
				}
			}
		}
	}
}
