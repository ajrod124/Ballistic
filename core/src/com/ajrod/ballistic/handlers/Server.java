package com.ajrod.ballistic.handlers;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class Server {

	public Server() {}
	
	public Response createUser(String username, int score) {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		RequestBody body = RequestBody.create(mediaType, "user=ajrod124&username=" + username + "&pass=hingadinga&score=" + score);
		Request request = new Request.Builder()
			.url("https://nameless-plains-6391.herokuapp.com/createUser")
			.post(body)
			.build();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Response updateFriends(String username, String friend) {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		RequestBody body = RequestBody.create(mediaType, "user=ajrod124&username=" + username + "&pass=hingadinga&friendName=" + friend);
		Request request = new Request.Builder()
			.url("https://nameless-plains-6391.herokuapp.com/updateFriends")
			.post(body)
			.build();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Response updateScore(String username, int score) {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		RequestBody body = RequestBody.create(mediaType, "user=ajrod124&username=" + username + "&pass=hingadinga&score=" + score);
		Request request = new Request.Builder()
			.url("https://nameless-plains-6391.herokuapp.com/updateScore")
			.post(body)
			.build();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Response retrieveUser(String username) {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		RequestBody body = RequestBody.create(mediaType, "user=ajrod124&username=" + username + "&pass=hingadinga");
		Request request = new Request.Builder()
			.url("https://nameless-plains-6391.herokuapp.com/retrieveUser")
			.post(body)
			.build();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Response retrieveTop20() {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		RequestBody body = RequestBody.create(mediaType, "user=ajrod124&pass=hingadinga");
		Request request = new Request.Builder()
			.url("https://nameless-plains-6391.herokuapp.com/retrieveTop20")
			.post(body)
			.build();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
