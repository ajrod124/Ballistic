package com.ajrod.ballistic.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricEncryptionUtility {
	
	private static String KEY = "39ae2bee4981bcf1";
	
	public static final String encrypt(final String message) throws IllegalBlockSizeException,
	BadPaddingException, NoSuchAlgorithmException,
	NoSuchPaddingException, InvalidKeyException,
	UnsupportedEncodingException, InvalidAlgorithmParameterException {
		
		SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
	    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, key);
	
	    byte[] stringBytes = message.getBytes();
	    byte[] raw = cipher.doFinal(stringBytes);
	
	    return (new String(raw, "Latin-1"));
	}
	
	public static final String decrypt(final String encrypted) throws InvalidKeyException,
	NoSuchAlgorithmException, NoSuchPaddingException,
	IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException {
		
		SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
	    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, key);
	
	    byte[] raw = encrypted.getBytes("Latin-1");
	    byte[] stringBytes = cipher.doFinal(raw);

	    String clearText = new String(stringBytes, "UTF8");
	    return clearText;
	      
	}
}
