package com.ajrod.ballistic.handlers;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.Input.TextInputListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FriendInput implements TextInputListener {

	private boolean success = false, completed = false;
	private String error = null;

	@Override
	public void input(String friendName) {
		
		if (friendName.length() == 0) {
			success = false;
			error = "none";
			completed = true;
			return;
		}
		
		try {
			JSONObject res = new JSONObject(Ballistic.server.updateFriends(Ballistic.username, friendName).body().string());
			error = res.getString("err");
			if (error.equals("none")) {
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
		error = "none";
		completed = true;
	}
	
	public boolean isSuccess() { return success; }
	public String getError() { return error; }
	public boolean isComplete() { return completed; }
	
	public void reset() {
		success = false;
		completed = false;
		error = null;
	}

}
