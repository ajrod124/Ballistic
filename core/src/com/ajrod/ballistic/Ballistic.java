package com.ajrod.ballistic;

import com.ajrod.ballistic.handlers.Content;
import com.ajrod.ballistic.handlers.Server;
import com.ajrod.ballistic.handlers.Sounds;
import com.ajrod.ballistic.handlers.SymmetricEncryptionUtility;
import com.ajrod.ballistic.states.GSM;
import com.ajrod.ballistic.states.MenuState;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Ballistic extends ApplicationAdapter {
	
	public static final String TITLE = "Ballistic";
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static int highScore;
	public static String username;
	public static Content res;
	public static BitmapFont font;
	public static Sounds sounds;
	public static Server server;
	public static Preferences prefs;
	public static boolean soundOn, onMenu;
	public static ActionResolver ar;
	
	private GSM gsm;
	private SpriteBatch sb;
	
	public Ballistic(ActionResolver ar) {
		this.ar = ar;
	}
	
	@Override
	public void create () {
		prefs = Gdx.app.getPreferences("Ballistic");
		
		if (prefs.contains("highScore"))
			highScore = prefs.getInteger("highScore");
		else {
			if (prefs.contains("score")) {
				if (prefs.contains("score")) {
					try {
						highScore = Integer.parseInt(SymmetricEncryptionUtility.decrypt(prefs.getString("score")));
					} catch (NumberFormatException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (InvalidKeyException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (NoSuchPaddingException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (IllegalBlockSizeException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (BadPaddingException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
						highScore = 0;
					} catch (IOException e) {
						e.printStackTrace();
						highScore = 0;
					}
				}
			}
			else highScore = 0;
		}
		
		if (prefs.contains("sound"))
			soundOn = prefs.getBoolean("sound");
		
		if (prefs.contains("username"))
			username = prefs.getString("username");
		else
			username = null;
		
		gsm = new GSM();
		server = new Server();
		res = new Content();
		sb = new SpriteBatch();
		sounds = new Sounds();
		font = new BitmapFont(Gdx.files.internal("flipps.fnt"), false);
		
		res.loadAtlas("pack.pack", "pack");
		onMenu = true;
		gsm.push(new MenuState(gsm));
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
	}

	@Override
	public void render () {	
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(sb);
	}
	
	public void dispose() {
		super.dispose();
	}
}
