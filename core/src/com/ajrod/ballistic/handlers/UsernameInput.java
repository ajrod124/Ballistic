package com.ajrod.ballistic.handlers;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.Input.TextInputListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UsernameInput implements TextInputListener {
	
	private boolean success = false, completed = false;
	private String error = null;

	@Override
	public void input(String name) {
		
		if (name.length() == 0) {
			success = false;
			error = "You must enter a name to continue!";
			completed = true;
			return;
		}
		
		try {
			JSONObject res = new JSONObject(Ballistic.server.createUser(name, Ballistic.highScore).body().string());
			error = res.getString("err");
			if (error.equals("none")) {
				Ballistic.username = name;
				Ballistic.prefs.putString("username", Ballistic.username);
				Ballistic.prefs.flush();
				success = true;
			}
			else
				success = false;
		} catch (JSONException e) {
			error = e.toString();
			success = false;
		} catch (IOException e) {
			error = e.toString();
			success = false;
		}

		completed = true;
	}

	public void canceled() {
		success = false;
		error = "You must enter a name to continue!";
		completed = true;
	}
	
	public boolean isSuccess() { return success; }
	public String getError() { return error; }
	public boolean isComplete() { return completed; }
}
