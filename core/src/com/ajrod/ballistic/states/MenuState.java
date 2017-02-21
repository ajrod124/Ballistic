package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.BasicMissile;
import com.ajrod.ballistic.gameobjects.Button;
import com.ajrod.ballistic.gameobjects.FragMissile;
import com.ajrod.ballistic.gameobjects.Graphic;
import com.ajrod.ballistic.gameobjects.PhasingMissile;
import com.ajrod.ballistic.gameobjects.ShieldedMissile;
import com.ajrod.ballistic.gameobjects.SoundButton;
import com.ajrod.ballistic.gameobjects.SpiralMissile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MenuState extends State {

	private Graphic title;
	private Button play, leader;
	private SoundButton sound;
	private TextureRegion bg;
	private BasicMissile m1;
	private ShieldedMissile m2;
	private FragMissile m3;
	private SpiralMissile m4;
	private PhasingMissile m5;
	private int timer;
	boolean startGame;

	public MenuState(GSM gsm) {
		super(gsm);
		Ballistic.onMenu = true;
		title = new Graphic(Ballistic.res.getAtlas("pack").findRegion("ballistic_icon"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2 + 100);
		play = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2 - 150, 153, 78);
		leader = new Button(Ballistic.res.getAtlas("pack").findRegion("leaderboards_button"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2 - 250, 153, 78);
		sound = new SoundButton(Ballistic.WIDTH - 25, 25, 50, 50, Ballistic.soundOn);
		Texture tex = new Texture(Gdx.files.internal("bg1.png"));
		bg = new TextureRegion(tex);
		m1 = new BasicMissile(80, false);
		m2 = new ShieldedMissile(80);
		m3 = new FragMissile(80);
		m4 = new SpiralMissile(80);
		m5 = new PhasingMissile(150);
		timer = 16;
		startGame = false;
	}

	@Override
	public void update(float dt) {
		if (!startGame) handleInput();
		else timer--;
		if (timer == 0) {
			gsm.set(new LevelOneState(gsm));
			//gsm.set(new TestState(gsm));
			Ballistic.prefs.putBoolean("sound", Ballistic.soundOn);
			Ballistic.prefs.flush();
		}
		m1.update(dt, 50);
		if (m1.getRadius() >= m1.getMaxRadius()) m1.reset(0);
		m2.update(dt, 30);
		if (m2.getRadius() >= m2.getMaxRadius()) m2.reset();
		m3.update(dt, 25);
		if (m3.getRadius() >= m3.getMaxRadius()) m3.reset();
		if (m3.isDead()) m3.kill();
		m4.update(dt, 80);
		if (m4.getRadius() >= m4.getMaxRadius()) m4.reset();
		m5.update(dt, 25);
		if (m5.getRadius() >= m5.getMaxRadius()) m5.reset();
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		sb.draw(bg, 0, 0, Ballistic.WIDTH, 1600);
		m1.render(sb);
		m2.render(sb);
		m3.render(sb);
		m4.render(sb);
		m5.render(sb);
		title.render(sb);
		play.render(sb);
		Ballistic.font.draw(sb, "Play", Ballistic.WIDTH/2 - 43, Ballistic.HEIGHT/2 - 135);
		leader.render(sb);
		sound.render(sb);
		sb.end();
	}

	@Override
	public void handleInput() {
		if(Gdx.input.justTouched()) {
			mouse.x = Gdx.input.getX();
			mouse.y = Gdx.input.getY();
			cam.unproject(mouse);
			if (sound.contains(mouse.x, mouse.y)) {
				if (Ballistic.soundOn) Ballistic.soundOn = false;
				else Ballistic.soundOn = true;
				sound.setState(Ballistic.soundOn);
			}
			if (play.contains(mouse.x, mouse.y)) {
				Ballistic.onMenu = false;
				startGame = true;
				m1.reset(0);
				m2.reset();
				m3.kill();
				m4.reset();
				m5.reset();
			}
			if (leader.contains(mouse.x, mouse.y)) {
				gsm.set(new LoadingState(gsm));
			}
		}
	}
	
}
