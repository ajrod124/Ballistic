package com.ajrod.ballistic.android;

import android.os.Bundle;

import com.ajrod.ballistic.ActionResolver;
import com.ajrod.ballistic.Ballistic;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
	
	/*private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"; // ca-app-pub-8360163844106968/9508139939 TODO: put this in instead of placeholder
	private InterstitialAd interstitialAd;*/
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        /*interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
        interstitialAd.setAdListener(new AdListener() {});*/

        initialize(new Ballistic(this), config);
	}

    public void showOrLoadInterstitial() {
        /*try {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                    else {
                        AdRequest interstitialRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build(); // TODO: remove test device
                        interstitialAd.loadAd(interstitialRequest);
                    }
                }
            });
        } catch (Exception e) {}*/
    }
}
