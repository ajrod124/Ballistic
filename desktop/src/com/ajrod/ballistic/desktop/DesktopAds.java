package com.ajrod.ballistic.desktop;

import com.ajrod.ballistic.ActionResolver;

public class DesktopAds implements ActionResolver {

	@Override
	public void showOrLoadInterstitial() {
		System.out.println("showOrLoadInterstital()");
	}

}
