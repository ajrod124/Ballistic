package com.ajrod.ballistic.gameobjects;

import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Modal {
	
	private TextureRegion box;
	private Button ok;
	private String title, body;
	private SpriteBatch batch = null;
	private int multiplier;
	private boolean error;
	
	public Modal(boolean error, String message) {
		
		multiplier = message.length() * 5;
		
		box = Ballistic.res.getAtlas("pack").findRegion("modal");
		ok = new Button(Ballistic.res.getAtlas("pack").findRegion("menubuttons"),
				240, Ballistic.HEIGHT/2 - 62, 100, 50);
		
		if (error)
			title = "ERROR";
		else
			title = "SUCCESS";
		body = message;
		
		this.error = error;
	}
	
	public void render(SpriteBatch sb) {
		if (batch == null) batch = sb;
		sb.setColor(1, 1, 1, 1);
		sb.draw(box, 40, 300, 400, 200);
		
		if (error)
			Ballistic.font.draw(sb, title, Ballistic.WIDTH/2 - 57, Ballistic.HEIGHT/2 + 90);
		else
			Ballistic.font.draw(sb, title, Ballistic.WIDTH/2 - 83, Ballistic.HEIGHT/2 + 90);
		Ballistic.font.getData().setScale(0.50f);
		Ballistic.font.draw(sb, body, Ballistic.WIDTH/2 - multiplier, Ballistic.HEIGHT/2 + 10);
		
		ok.render(sb);
		Ballistic.font.getData().setScale(1f);
		Ballistic.font.draw(sb, "OK", Ballistic.WIDTH/2 - 25, Ballistic.HEIGHT/2 - 48);
		
		sb.setColor(1, 1, 1, 0.5f);
	}
	
	public boolean okClicked(float x, float y) { 
		if (ok.contains(x, y)) {
			batch.setColor(1, 1, 1, 1);
			return true;
		}
		else
			return false;
	}
	
	public boolean isError() { return error; }
}
