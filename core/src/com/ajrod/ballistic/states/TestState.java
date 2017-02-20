package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.ThirdBoss;
import com.ajrod.ballistic.states.LevelState.Running;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TestState extends LevelState {

	private TextureRegion white;
	private ThirdBoss boss;

	public TestState(GSM gsm) { 
		super(gsm);
		
		tex = new Texture(Gdx.files.internal("white.png"));
		white = new TextureRegion(tex);
		boss = new ThirdBoss();
	}

	public void update(float dt) {
		if (running == Running.RESUMED) {
			if (!dead) handleInput();
			if (dead)
				gameOver(dt);
			else if (!done) {
				if (timer < 2) startPhase(dt);
				else {
					boss.update(dt);
				}
			}
			else { endPhase(dt); }
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		sb.draw(white, 0, 0, Ballistic.WIDTH, 150);
		sb.draw(white, 0, 150, 150, Ballistic.HEIGHT - 300);
		sb.draw(white, Ballistic.WIDTH - 150, 150, 150, Ballistic.HEIGHT - 300);
		sb.draw(white, 0, Ballistic.HEIGHT - 150, Ballistic.WIDTH, 150);
		boss.render(sb);
		sb.end();
	}

	public void handleInput() {
	}
	
	protected void startPhase(float dt) {
		boss.startPhase(dt);
		timer += dt;
	}

	protected void endPhase(float dt) {
	}
	
	protected void gameOver(float dt) {
	}
	
	protected void setDeathParams(float x, float y, float rad) {
	}

	@Override
	protected void drawUI(SpriteBatch sb) {
		// TODO Auto-generated method stub
		
	}
}
