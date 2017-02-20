package com.ajrod.ballistic.gameobjects;

import java.util.Random;

import com.ajrod.ballistic.Ballistic;
import com.ajrod.ballistic.gameobjects.ThirdBossMinions.Coords;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ThirdBoss extends Circle {
	
	private ThirdBossMinions[] minions = new ThirdBossMinions[10];
	private Animation ani;
	private TextureRegion[] missileFrames;
	private TextureRegion currentFrame, healthBar, health, barEnd;
	private int hp, direction, turn, count;
	private boolean defeated, hit, changing, right, left, top, bottom, middle;
	private float stateTime, tempX, tempY, angle, deathRad, deathX, deathY;
	private double timer, midTimer, disTimer;
	private Explosion explosion;
	private Random rand;
	private Coords temp;
	
	public ThirdBoss() {
		temp = null; count = 10;
		timer = 0; midTimer = 0; turn = 0; disTimer = 0;
		changing = false; right = false; left = false; 
		top = false; bottom = false; middle = false;
		rand = new Random();
		closeness = 19;
		initialize(rand.nextInt(6));
		for (int i = 0; i < 10; i++)
			minions[i] = new ThirdBossMinions(80, x, y);
		defeated = false;
		hit = false;
		explosion = new Explosion(x, y);
		hp = 200;
		//hp = 20; // testing purposes
		radius = 50;
		
		healthBar = Ballistic.res.getAtlas("pack").findRegion("HealthBar");
		barEnd = Ballistic.res.getAtlas("pack").findRegion("HealthEnd");
		health = Ballistic.res.getAtlas("pack").findRegion("Health");
		
		missileFrames = new TextureRegion[6];
		TextureRegion[][] tmp = Ballistic.res.getAtlas("pack").findRegion("boss_3_ship").split(50, 50);
		int k = 0;
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 3; j++) {
				missileFrames[k] = tmp[i][j];
				k++;
			}
		
		ani = new Animation(0.1f, missileFrames);
		ani.setPlayMode(PlayMode.LOOP_PINGPONG);
		stateTime = 0f;
	}
	
	public void onClick() {
		if (Ballistic.soundOn)
			Ballistic.sounds.hit.play(1.0f);
		hp--;
		hit = true;
		if (hp == 0) {
			if (Ballistic.soundOn)
				Ballistic.sounds.hit.play(1.0f);
			reset(); 
		}
	}
	
	public void update(float dt) {
		explosion.update(dt);
		stateTime += dt;
		if (stateTime > 0.29f) stateTime = 0;
		if (y >= Ballistic.HEIGHT - 150) {
			if (middle) middle(dt);
			else if (right) rightEdge(dt);
			else if (left) leftEdge(dt);
			else topEdge(dt);
		}
		else if (y <= 150) {
			if (middle) middle(dt);
			else if (right) rightEdge(dt);
			else if (left) leftEdge(dt);
			else bottomEdge(dt);
		}
		else {
			if (x >= Ballistic.WIDTH - 150) {
				if (middle) middle(dt);
				else if (top) topEdge(dt);
				else if (bottom) bottomEdge(dt);
				else rightEdge(dt);
			}
			else if (x <= 150) {
				if (middle) middle(dt);
				else if (top) topEdge(dt);
				else if (bottom) bottomEdge(dt);
				else leftEdge(dt);
			}
			else middle(dt);
		}
		tempX = x; tempY = y;
		for (int i = 0; i < 10; i++) {
			temp = minions[i].update(dt, 15, tempX, tempY);
			if (temp != null) {
				tempX = temp.getX(); tempY = temp.getY();
			}
		}
		if (count > 0) {
			disTimer += dt;
			if (disTimer > 5f) { 
				minions[--count].disengage(); 
				disTimer = 0;
			}
		}
	}
	
	public void render(SpriteBatch sb) {
			explosion.render(sb);
			if (!hit) currentFrame = ani.getKeyFrame(stateTime, true);
			else {
				currentFrame = ani.getKeyFrame(stateTime + 0.3f, true);
				hit = false;
			}
			sb.draw(currentFrame, x - (float)radius/2, y - (float)radius/2, (float)radius/2,  (float)radius/2, (float)radius, (float)radius, 1f, 1f, angle);
			
			if (hp > 0) {
				sb.draw(healthBar, 32, Ballistic.HEIGHT - 29, 416, 20);
				sb.draw(barEnd, 40 + (hp/2)*4, Ballistic.HEIGHT - 25, 4, 12);
				sb.draw(health, 40, Ballistic.HEIGHT - 25, (hp/2)*4, 12);
			}
			for (int i = 0; i < 10; i++) minions[i].render(sb);
	}
	
	public void reset() { 
		if (hp == 0) {
			explosion.setXY(x, y);
			explosion.setWH((float)radius);
			radius = 0;
			defeated = true;
		}
		for (int i = 0; i < 10; i++) minions[i].reset();
	}
	
	public boolean killedU() {
		for (int i = 0; i < 10; i++) {
			if (minions[i].getRadius() >= minions[i].getMaxRadius()) {
				setDeathParams(minions[i].getX(), minions[i].getY(), (float)minions[i].getRadius()*6);
				return true;
			}
		}
		return false;
	}
	
	private void setDeathParams(float x, float y, float rad) {
		deathX = x;
		deathY = y;
		deathRad = rad;
	}
	
	public float[] getDeathParams() { 
		float[] tmp = {deathX, deathY, deathRad};
		return tmp;
	}
	
	public boolean isDefeated() { return defeated; }
	
	public void miniClicked(float x, float y) {
		for (int i = 0; i < 10; i++) {
			if (minions[i].contains(x, y)) {
				minions[i].onClick();
			}
		}
	}
	
	private void topEdge(float dt) {
		switch (direction) {
		case 0:
			if (!top) top = true;
			changeDirection(dt, 4);
			if (!changing) {
				direction = 4;
				angle = 180f;
				timer = 0;
				top = false;
			}
			break;
		case 1:
			if (!top) top = true;
			changeDirection(dt, 3);
			if (!changing) {
				direction = 3;
				angle = 225f;
				timer = 0;
				top = false;
			}
			break;
		case 2:
			if (!top) top = true;
			changeDirection(dt, 1);
			if (!changing) {
				direction = 3;
				angle = 225f;
				timer = 0;
				top = false;
			}
			break;
		case 3:
			if (top) top = false;
			move(dt);
			break;
		case 4:
			if (top) top = false;
			move(dt);
			break;
		case 5:
			if (top) top = false;
			move(dt);
			break;
		case 6:
			if (!top) top = true;
			changeDirection(dt, 0);
			if (!changing) {
				direction = 5;
				angle = 135f;
				timer = 0;
				top = false;
			}
			break;
		case 7:
			if (!top) top = true;
			changeDirection(dt, 2);
			if (!changing) {
				direction = 5;
				angle = 135f;
				timer = 0;
				top = false;
			}
			break;
		}
	}
	
	private void rightEdge(float dt) {
		switch (direction) {
		case 0:
			if (!right) right =  true;
			changeDirection(dt, 0);
			if (!changing) {
				direction = 7;
				angle = 45f;
				timer = 0;
				right = false;
			}
			break;
		case 1:
			if (!right) right =  true;
			changeDirection(dt, 2);
			if (!changing) {
				direction = 7;
				angle = 45f;
				timer = 0;
				right = false;
			}
			break;
		case 2:
			if (!right) right =  true;
			changeDirection(dt, 4);
			if (!changing) {
				direction = 6;
				angle = 90f;
				timer = 0;
				right = false;
			}
			break;
		case 3:
			if (!right) right =  true;
			changeDirection(dt, 3);
			if (!changing) {
				direction = 5;
				angle = 135f;
				timer = 0;
				right = false;
			}
			break;
		case 4:
			if (!right) right =  true;
			changeDirection(dt, 1);
			if (!changing) {
				direction = 5;
				angle = 135f;
				timer = 0;
				right = false;
			}
			break;
		case 5:
			if (right) right = false;
			move(dt);
			break;
		case 6:
			if (right) right = false;
			move(dt);
			break;
		case 7:
			if (right) right = false;
			move(dt);
			break;
		}
	}
	
	private void leftEdge(float dt) {
		switch (direction) {
		case 0:
			if (!left) left = true;
			changeDirection(dt, 1);
			if (!changing) {
				direction = 1;
				angle = 315f;
				timer = 0;
				left = false;
			}
			break;
		case 1:
			if (left) left = false;
			move(dt);
			break;
		case 2:
			if (left) left = false;
			move(dt);
			break;
		case 3:
			if (left) left = false;
			move(dt);
			break;
		case 4:
			if (!left) left = true;
			changeDirection(dt, 0);
			if (!changing) {
				direction = 3;
				angle = 225f;
				timer = 0;
				left = false;
			}
			break;
		case 5:
			if (!left) left = true;
			changeDirection(dt, 2);
			if (!changing) {
				direction = 3;
				angle = 225f;
				timer = 0;
				left = false;
			}
			break;
		case 6:
			if (!left) left = true;
			changeDirection(dt, 4);
			if (!changing) {
				direction = 2;
				angle = 270f;
				timer = 0;
				left = false;
			}
			break;
		case 7:
			if (!left) left = true;
			changeDirection(dt, 3);
			if (!changing) {
				direction = 1;
				angle = 315f;
				timer = 0;
				left = false;
			}
			break;
		}
	}
	
	private void bottomEdge(float dt) {
		switch (direction) {
		case 0:
			if (bottom) bottom = false;
			move(dt);
			break;
		case 1:
			if (bottom) bottom = false;
			move(dt);
			break;
		case 2:
			if (!bottom) bottom = true;
			changeDirection(dt, 0);
			if (!changing) {
				direction = 1;
				angle = 315f;
				timer = 0;
				bottom = false;
			}
			break;
		case 3:
			if (!bottom) bottom = true;
			changeDirection(dt, 2);
			if (!changing) {
				direction = 1;
				angle = 315f;
				timer = 0;
				bottom = false;
			}
			break;
		case 4:
			if (!bottom) bottom = true;
			changeDirection(dt, 4);
			if (!changing) {
				direction = 0;
				angle = 0f;
				timer = 0;
				bottom = false;
			}
			break;
		case 5:
			if (!bottom) bottom = true;
			changeDirection(dt, 3);
			if (!changing) {
				direction = 7;
				angle = 45f;
				timer = 0;
				bottom = false;
			}
			break;
		case 6:
			if (!bottom) bottom = true;
			changeDirection(dt, 1);
			if (!changing) {
				direction = 7;
				angle = 45f;
				timer = 0;
				bottom = false;
			}
			break;
		case 7:
			if (bottom) bottom = false;
			move(dt);
			break;
		}
	}
	
	private void middle(float dt) {
		if (!changing || middle) {
			if (!changing) {
				move(dt);
				midTimer += dt;
			}
			if (midTimer > 1) {
				if (!middle) {
					middle = true;
					turn = rand.nextInt(8);
					midTimer = 10;
				}
				changeDirection(dt, turn);
				if (!changing) {
					switch (turn) {
					case 0:
						direction = (direction + 7)%8;
						break;
					case 1:
						direction = (direction + 1)%8;
						break;
					case 2:
						direction = (direction + 6)%8;
						break;
					case 3:
						direction = (direction + 2)%8;
						break;
					case 4:
						direction = (direction + 4)%8;
						break;
					case 5:
						direction = (direction + 4)%8;
						break;
					case 6:
						direction = (direction + 2)%8;
						break;
					case 7:
						direction = (direction + 6)%8;
						break;
					}
					setAngle();
					timer = 0;
					midTimer = 0;
					middle = false;
				}
			}
		}
		else {
			if (top) topEdge(dt);
			else if (right) rightEdge(dt);
			else if (bottom) bottomEdge(dt);
			else if (left) leftEdge(dt);
		}
	}
	
	public void startPhase(float dt) {
		explosion.update(dt);
		stateTime += dt;
		if (stateTime > 0.29f) stateTime = 0;
		move(dt);
		tempX = x; tempY = y;
		for (int i = 0; i < 10; i++) {
			temp = minions[i].update(dt, 15, tempX, tempY);
			if (temp == null) break;
			else {
				tempX = temp.getX(); tempY = temp.getY();
			}
		}
	}

	private void initialize(int dir) {
		switch (dir) {
		case 0:
			x = -50;
			y = Ballistic.HEIGHT + 50;
			direction = 3;
			angle = 225f;
			break;
		case 1:
			x = -50;
			y = Ballistic.HEIGHT/2;
			direction = 2;
			angle = 270f;
			break;
		case 2:
			x = -50;
			y = -50;
			direction = 1;
			angle = 315f;
			break;
		case 3:
			x = Ballistic.WIDTH + 50;
			y = Ballistic.HEIGHT + 50;
			direction = 5;
			angle = 135f;
			break;
		case 4:
			x = Ballistic.WIDTH + 50;
			y = Ballistic.HEIGHT/2;
			direction = 6;
			angle = 90f;
			break;
		case 5:
			x = Ballistic.WIDTH + 50;
			y = -50;
			direction = 7;
			angle = 45f;
			break;
		}
	}
	
	private void move(float dt) {
		switch (direction) {
		case 0:	
			y += dt*180;
			break;
		case 1:	
			x += dt*120;
			y += dt*120;
			break;	
		case 2:	
			x += dt*180;
			break;
		case 3:	
			x += dt*120;
			y -= dt*120;
			break;		
		case 4:	
			y -= dt*180;
			break;
		case 5:	
			x -= dt*120;
			y -= dt*120;
			break;		
		case 6:	
			x -= dt*180;
			break;
		case 7:	
			x -= dt*120;
			y += dt*120;
			break;
		}
	}
	
	/* Range:	0 -> 1/8 turn CCW  		1 -> 1/8 turn CW		*
	 * 			2 -> quarter turn CCW	3 -> quarter turn CCW	*
	 * 			4 -> half turn CW		5 -> half turn CW		*
	 * 			6 -> 3/4 turn CW		7 -> 3/4 turn CW		*/
	private void changeDirection(float dt, int range) {
		if (!changing) changing = true;
		double dir;
		switch (direction) {
		case 0:
			dir = Math.PI/2;
			break;
		case 1:
			dir = Math.PI/4;
			break;
		case 2:
			dir = 0;
			break;
		case 3:
			dir = 7*Math.PI/4;
			break;
		case 4:
			dir = 3*Math.PI/2;
			break;
		case 5:
			dir = 5*Math.PI/4;
			break;
		case 6:
			dir = Math.PI;
			break;
		case 7:
			dir = 3*Math.PI/4;
			break;
		default:
			dir = 0;
			break;
		}
		switch (range) {
		case 0: //1/8 turn CCW
			if (timer < Math.PI/4) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer += 4*dt;
				angle += (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 1: //1/8 turn CW
			if (timer > -Math.PI/4) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer -= 4*dt;
				angle -= (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 2: //quarter turn CCW
			if (timer < Math.PI/2) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer += 4*dt;
				angle += (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 3: //quarter turn CW
			if (timer > -Math.PI/2) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer -= 4*dt;
				angle -= (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 4: //half turn CCW
			if (timer < Math.PI) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer += 4*dt;
				angle += (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 5: //half turn CW
			if (timer > -Math.PI) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer -= 4*dt;
				angle -= (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 6: //3/4 turn CCW
			if (timer < 3*Math.PI/2) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer += 4*dt;
				angle += (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		case 7: //3/4 turn CW
			if (timer > -3*Math.PI/2) { // range
				x += 3*Math.cos(timer + dir); //start direction
				y += 3*Math.sin(timer + dir);
				timer -= 4*dt;
				angle -= (720*dt)/Math.PI;
			}
			else changing = false;
			break;
		}
	}
	
	private void setAngle() {
		switch (direction) {
		case 0:
			angle = 0;
			return;
		case 1:
			angle = 315;
			return;
		case 2:
			angle = 270;
			return;
		case 3:
			angle = 225;
			return;
		case 4:
			angle = 180;
			return;
		case 5:
			angle = 135;
			return;
		case 6:
			angle = 90;
			return;
		case 7:
			angle = 45;
			return;
		}
	}
	
	public float getX() { return x; }
	public float getY() { return y; }
}

