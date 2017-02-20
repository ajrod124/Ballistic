package com.ajrod.ballistic.states;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GSM {

	private Stack<State> states;
	
	public GSM() { states = new Stack<State>(); }
	
	public void push(State s) { states.push(s); }
	
	public void pop() { states.pop(); }
	
	public void set(State s) {
		states.pop();
		states.push(s);
	}
	
	public void update(float dt) { //if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
		states.peek().update(dt); 
	}
	
	public void render(SpriteBatch sb) { states.peek().render(sb); }
}
