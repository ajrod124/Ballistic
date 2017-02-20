package com.ajrod.ballistic.states;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.Button;
import com.ajrod.ballistic.gameobjects.Modal;
import com.ajrod.ballistic.handlers.FriendInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Arrays;

public class LeaderboardState extends State {

	private TextureRegion bg;
	private Button friends, global, menu, addFriend;
	private FriendInput fInput;
	private boolean isGlobal, modalActive;
	private Modal modal;
	
	private ArrayList<String> top20Names, top20Scores;
	private class Friends implements Comparable<Friends> {
		public String name;
		public int score;
		
		public Friends (String n, int s){
			name = n; score = s;
		}
		
		@Override
		public int compareTo(Friends that) {
			if (this.score < that.score) return 1;
			if (this.score > that.score) return -1;
			return 0;
		}
		
	}
	private Friends[] friendsList;
	
	public LeaderboardState(GSM gsm, ArrayList<String> fn, ArrayList<Number> fs, ArrayList<String> tn, ArrayList<String> ts) {
		super(gsm);
		
		top20Names = new ArrayList<String>(tn);
		top20Scores = new ArrayList<String>(ts);;
		
		friendsList = new Friends[fs.size() + 1];
		for (int i = 0; i < friendsList.length - 1; i++)
			friendsList[i] = new Friends(fn.get(i), fs.get(i).intValue());
		friendsList[friendsList.length - 1] = new Friends(Ballistic.username, Ballistic.highScore);
		
		Arrays.sort(friendsList);
		
		menu = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
			82.5f, 45, 150, 75);
		addFriend = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
			397.5f, Ballistic.HEIGHT - 120, 150, 75);
		global = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
			82.5f, Ballistic.HEIGHT - 120, 150, 75);
		friends = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
			240, Ballistic.HEIGHT - 120, 150, 75);
		Texture tex = new Texture(Gdx.files.internal("bg1.png"));
		bg = new TextureRegion(tex);
		
		fInput = new FriendInput();
		
		isGlobal = true;
		modalActive = false;
		
	}

	public void update(float dt) {
		handleInput();
		if (!modalActive) {
			if (fInput.isComplete() && fInput.isSuccess()) {
				modal = new Modal(false, "Friend successfully added!");
				modalActive = true;
				System.out.println("Friend successfully added!");
			}
			else if (fInput.isComplete() && !fInput.isSuccess() && !fInput.getError().equals("none")) {
				modal = new Modal(true, fInput.getError());
				modalActive = true;
				System.out.println(fInput.getError());
			}
		}
	}

	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		sb.draw(bg, 0, 0, Ballistic.WIDTH, 1600);
		menu.render(sb);
		addFriend.render(sb);
		global.render(sb);
		friends.render(sb);
		Ballistic.font.draw(sb, "Menu", 32, 60);
		if (isGlobal) {
			Ballistic.font.setColor(1.0f, 0.5f, 0, 1.0f);
			Ballistic.font.draw(sb, "Global", 22, Ballistic.HEIGHT - 105);
			Ballistic.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		else
			Ballistic.font.draw(sb, "Global", 22, Ballistic.HEIGHT - 105);
		Ballistic.font.getData().setScale(1.6f);
		Ballistic.font.draw(sb, "Leaderboards", 20, Ballistic.HEIGHT - 10);
		Ballistic.font.getData().setScale(0.8f);
		
		if (isGlobal) {
			for (int i = 0; i < top20Names.size(); i++) {
				if (top20Names.get(i).equals(Ballistic.username))
					Ballistic.font.setColor(1.0f, 0.5f, 0, 1.0f);
				if (i < 9)
					Ballistic.font.draw(sb, (i + 1) + ".  " + top20Names.get(i), 10, (Ballistic.HEIGHT - 174) - (i*27));
				else
					Ballistic.font.draw(sb, (i + 1) + ". " + top20Names.get(i), 10, (Ballistic.HEIGHT - 174) - (i*27));
				Ballistic.font.draw(sb, top20Scores.get(i), Ballistic.WIDTH - 110, (Ballistic.HEIGHT - 174) - (i*27));
				if (top20Names.get(i).equals(Ballistic.username))
					Ballistic.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		else {
			for (int i = 0; i < friendsList.length; i++) {
				if (friendsList[i].name.equals(Ballistic.username))
					Ballistic.font.setColor(1.0f, 0.5f, 0, 1.0f);
				if (i < 9)
					Ballistic.font.draw(sb, (i + 1) + ".  " + friendsList[i].name, 10, (Ballistic.HEIGHT - 174) - (i*27));
				else
					Ballistic.font.draw(sb, (i + 1) + ". " + friendsList[i].name, 10, (Ballistic.HEIGHT - 174) - (i*27));
				Ballistic.font.draw(sb, friendsList[i].score + "", Ballistic.WIDTH - 110, (Ballistic.HEIGHT - 174) - (i*27));
				if (friendsList[i].name.equals(Ballistic.username))
					Ballistic.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		
		Ballistic.font.getData().setScale(0.9f);
		if (!isGlobal) {
			Ballistic.font.setColor(1.0f, 0.5f, 0, 1.0f);
			Ballistic.font.draw(sb, "Friends", 175, Ballistic.HEIGHT - 105);
			Ballistic.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		else
			Ballistic.font.draw(sb, "Friends", 175, Ballistic.HEIGHT - 105);
		Ballistic.font.draw(sb, "Add", Ballistic.WIDTH - 117, Ballistic.HEIGHT - 90);
		Ballistic.font.draw(sb, "Friend", Ballistic.WIDTH - 137, Ballistic.HEIGHT - 120);
		Ballistic.font.getData().setScale(1);
		if (modalActive) modal.render(sb);
		sb.end();
	}

	public void handleInput() {
		if(Gdx.input.justTouched()) {
			mouse.x = Gdx.input.getX();
			mouse.y = Gdx.input.getY();
			cam.unproject(mouse);
			if (modalActive) {
				if (modal.okClicked(mouse.x, mouse.y)) {
					if (modal.isError()) {
						fInput.reset();
						modalActive = false;
						modal = null;
					}
					else gsm.set(new LoadingState(gsm));
				}
			}
			else {
				if (menu.contains(mouse.x, mouse.y))
					gsm.set(new MenuState(gsm));
				if (global.contains(mouse.x, mouse.y) && !isGlobal) {
					isGlobal = true;
				}
				if (friends.contains(mouse.x, mouse.y) && isGlobal) {
					isGlobal = false;
				}
				if (addFriend.contains(mouse.x, mouse.y)) {
					Gdx.input.getTextInput(fInput, "Enter Your Friend's Username", null, null);
				}
			}
		}
	}

}
