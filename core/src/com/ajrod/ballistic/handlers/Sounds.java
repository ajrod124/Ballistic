package com.ajrod.ballistic.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
	
	public final Sound hit, explosion, pu1, pu2, pu3, song, bigExplosion, shot, dead, chargingUp, warped;
	
	public Sounds() {
		hit = Gdx.audio.newSound(Gdx.files.internal("Hit_Hurt.wav"));
		explosion = Gdx.audio.newSound(Gdx.files.internal("Explosion.wav"));
		pu1 = Gdx.audio.newSound(Gdx.files.internal("BigGuns.wav"));
		pu2 = Gdx.audio.newSound(Gdx.files.internal("Powerup.wav"));
		pu3 = Gdx.audio.newSound(Gdx.files.internal("Powerup2.wav"));
		song = Gdx.audio.newSound(Gdx.files.internal("boss_song.wav"));
		bigExplosion = Gdx.audio.newSound(Gdx.files.internal("FinalExplosion.wav"));
		shot = Gdx.audio.newSound(Gdx.files.internal("Laser_Shoot.wav"));
		dead = Gdx.audio.newSound(Gdx.files.internal("Dead.wav"));
		chargingUp = Gdx.audio.newSound(Gdx.files.internal("ChargingUp.wav"));
		warped = Gdx.audio.newSound(Gdx.files.internal("Warped.wav"));
	}
}
