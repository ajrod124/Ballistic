package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.Button;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverState extends State{

	private Button play, menu;
	private boolean flag, newHighScore;
	private int score;
	
	protected GameOverState(GSM gsm, int score) {
		super(gsm);
		this.score = score;
		
		if (Ballistic.highScore < score) {
			newHighScore = true;
			Ballistic.highScore = score;
			Ballistic.prefs.putInteger("highScore", Ballistic.highScore);
			Ballistic.prefs.flush();
		}
		
		play = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2 - 150, 153, 78);
		menu = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2 - 250, 153, 78);
		flag = true;

		Ballistic.ar.showOrLoadInterstitial();
	}

	@Override
	public void update(float dt) { handleInput(); }

	@Override
	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		if (flag) {
			sb.setColor(1, 1, 1, 1);
			flag = false;
		}
		Ballistic.font.getData().setScale(4);
		Ballistic.font.draw(sb, "GAME", Ballistic.WIDTH/2 - 200, Ballistic.HEIGHT/2 + 250);
		Ballistic.font.draw(sb, "OVER", Ballistic.WIDTH/2 - 195, Ballistic.HEIGHT/2 + 150);
		Ballistic.font.getData().setScale(1);
		
		if (newHighScore) {
			Ballistic.font.draw(sb, "New High Score: " + score, 65, Ballistic.HEIGHT/2 - 10);
		}
		else {
			Ballistic.font.draw(sb, String.format("        Score: %d", score), 50, Ballistic.HEIGHT/2 + 5);
			Ballistic.font.draw(sb, String.format("High Score: %d", Ballistic.highScore), 50, Ballistic.HEIGHT/2 - 35);
		}
		
		play.render(sb);
		Ballistic.font.draw(sb, "Play", Ballistic.WIDTH/2 - 43, Ballistic.HEIGHT/2 - 135);
		menu.render(sb);
		Ballistic.font.draw(sb, "Menu", Ballistic.WIDTH/2 - 50, Ballistic.HEIGHT/2 - 235);
		sb.end();
	}

	@Override
	public void handleInput() {
		if(Gdx.input.justTouched()) {
			mouse.x = Gdx.input.getX();
			mouse.y = Gdx.input.getY();
			cam.unproject(mouse);
			if (play.contains(mouse.x, mouse.y))
				gsm.set(new LevelOneState(gsm));
			if (menu.contains(mouse.x, mouse.y))
				gsm.set(new MenuState(gsm));
		}
	}

}
