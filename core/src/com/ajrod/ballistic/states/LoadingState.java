package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.Button;
import com.ajrod.ballistic.gameobjects.LoadingMissile;
import com.ajrod.ballistic.gameobjects.Modal;
import com.ajrod.ballistic.handlers.Server;
import com.ajrod.ballistic.handlers.UsernameInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class LoadingState extends State {
	
	private class LoadingThread implements Runnable {

		private Server server;
		private String username, error[];
		private ArrayList<String> friendNames, top20Names, top20Scores;
		private ArrayList<Number> friendScores;
		private int score;
		private boolean success[];
		
		public LoadingThread(Server server, String username, int score) {
			this.server = server;
			this.username = username;
			this.score = score;
			
			error = new String[3];
			success = new boolean[3];
			
			friendNames = new ArrayList<String>();
			friendScores = new ArrayList<Number>();
			top20Names = new ArrayList<String>();
			top20Scores = new ArrayList<String>();
		}
		
		@Override
		public void run() {
			// 1. Update Score
			try {
				JSONObject res = new JSONObject(server.updateScore(username, score).body().string());
				error[0] = res.getString("err");
				if (error[0].equals("none")) {
					success[0] = true;
				}
				else
					success[0] = false;
			} catch (JSONException e) {
				error[0] = e.toString();
				success[0] = false;
			} catch (IOException e) {
				error[0] = e.toString();
				success[0] = false;
			}
			// 2. Get user info
			try {
				JSONObject res = new JSONObject(server.retrieveUser(username).body().string());
				error[1] = res.getString("err");
				if (error[1].equals("none")) {
					JSONObject user = res.getJSONObject("res");
					JSONArray friends = user.getJSONArray("friends");
					for (int i = 0; i < friends.length(); i++) {
						JSONObject friend = friends.getJSONObject(i);
						friendNames.add(friend.getString("username"));
						friendScores.add(friend.getInt("score"));
					}
					
					success[1] = true;
				}
				else
					success[1] = false;
			} catch (JSONException e) {
				error[1] = e.toString();
				success[1] = false;
			} catch (IOException e) {
				error[1] = e.toString();
				success[1] = false;
			}
			// 3. Get top 20
			try {
				JSONObject res = new JSONObject(server.retrieveTop20().body().string());
				error[2] = res.getString("err");
				if (error[2].equals("none")) {
					
					JSONArray users = res.getJSONArray("res");
					for (int i = 0; i < users.length(); i++) {
						JSONObject friend = users.getJSONObject(i);
						top20Names.add(friend.getString("username"));
						top20Scores.add(friend.getInt("score") + "");
					}
					
					success[2] = true;
				}
				else
					success[2] = false;
			} catch (JSONException e) {
				error[2] = e.toString();
				success[2] = false;
			} catch (IOException e) {
				error[2] = e.toString();
				success[2] = false;
			}

			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					fn = friendNames;
					fs = friendScores;
					tn = top20Names;
					ts = top20Scores;
					err = error;
					assetsLoaded = true;
				}
			});
		}
		
	}
	
	private Button loading;
	private LoadingMissile missile;
	private TextureRegion bg;
	private UsernameInput box;
	private Modal modal;
	private boolean threadLaunched, assetsLoaded, modalActive;
	private ArrayList<String> fn, tn, ts;
	private ArrayList<Number> fs;
	private String[] err;

	public LoadingState(GSM gsm) {
		super(gsm);
		
		fn = new ArrayList<String>();
		fs = new ArrayList<Number>();
		tn = new ArrayList<String>();
		ts = new ArrayList<String>();
		err = new String[3];
		
		threadLaunched = false;
		assetsLoaded = false;
		modalActive = false;
		
		box = new UsernameInput();
		loading = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
				Ballistic.WIDTH/2, Ballistic.HEIGHT/2, 153, 78);
		missile = new LoadingMissile();
		Texture tex = new Texture(Gdx.files.internal("bg1.png"));
		bg = new TextureRegion(tex);
		
		createConnection();
	}

	public void update(float dt) {
		if (modalActive) handleInput();
		else {
			if (box.isComplete() && box.isSuccess() && !threadLaunched)
				createConnection();
			else if (box.isComplete() && !box.isSuccess()) {
				modal = new Modal(true, box.getError());
				modalActive = true;
				System.out.println(box.getError());
			}
			
			if (assetsLoaded) {
				String errMessage = "";
				boolean b = true;
				for (int i = 0; i < 3; i++) {
					if (!err[i].equals("none")) {
						errMessage += err[i] + ", ";
						System.out.println(err[i] + "Error: " + i);
						b = false;
					}
				}
				if (b)
					gsm.set(new LeaderboardState(gsm, fn, fs, tn, ts));
				else {
					modal = new Modal(true, errMessage);
					modalActive = true;
				}
			}
			missile.update(dt);
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		sb.draw(bg, 0, 0, Ballistic.WIDTH, 1600);
		loading.render(sb);
		Ballistic.font.getData().setScale(0.5f);
		Ballistic.font.draw(sb, "Loading...", Ballistic.WIDTH/2 - 47, Ballistic.HEIGHT/2 + 30);
		Ballistic.font.getData().setScale(1);
		missile.render(sb);
		if (modalActive) modal.render(sb);
		sb.end();
	}

	public void handleInput() {
		if(Gdx.input.justTouched()) {
			mouse.x = Gdx.input.getX();
			mouse.y = Gdx.input.getY();
			cam.unproject(mouse);
			if (modal.okClicked(mouse.x, mouse.y))
				gsm.set(new MenuState(gsm));
		}
	}
	
	private void createConnection() {
		if (Ballistic.username == null)
			Gdx.input.getTextInput(box, "Enter Your Username (Max Length 12)", null, null);
		else {
			threadLaunched = true;
			Runnable r = new LoadingThread(Ballistic.server, Ballistic.username, Ballistic.highScore);
			new Thread(r).start();
		}
	}
}
