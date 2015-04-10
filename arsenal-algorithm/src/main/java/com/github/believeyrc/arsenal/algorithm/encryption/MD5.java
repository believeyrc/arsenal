package com.github.believeyrc.arsenal.algorithm.encryption;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	private static final String CHARSET = "UTF-8";
	
	
	/**
	 * 
	 *
	 * @author yangrucheng
	 * @created 2015年4月7日 下午2:28:00
	 *
	 * @param key
	 * @return
	 */
	public static String getMD5(String key) {
		if (key == null) {
			return null; 
		}
		String hash = "";
		try {
			MessageDigest ms = MessageDigest.getInstance("md5");
			ms.update(key.getBytes(CHARSET));
			byte[] bytes = ms.digest();
			hash = toHex(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hash;
	}
	
	/**
	 * 
	 *
	 * @author yangrucheng
	 * @created 2015年4月7日 下午2:28:07
	 *
	 * @param bytes
	 * @return
	 */
	public static String toHex(byte[] bytes) {
		StringBuilder hex = new StringBuilder();
		for (byte b : bytes) {
			int i = b & 0xff;
			if (i < 16) {
				hex.append(0);
			}
			hex.append(Integer.toHexString(i));
		}
		
		return hex.toString().toUpperCase();
	}
}
