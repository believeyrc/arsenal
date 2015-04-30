package com.github.believeyrc.arsenal.algorithm;

import org.junit.Test;

import com.github.believeyrc.arsenal.algorithm.encryption.BCrypt;

public class EncryptionTest {

	@Test
	public void BCryptTest() {
		String password = "hello world!";
		String salt = BCrypt.gensalt(12);
		String hashpw = BCrypt.hashpw(password, salt);
		System.out.println("hashpw length :" + hashpw.length());
		System.out.println("hashpw : " + hashpw);
		boolean check = BCrypt.checkpw(password, hashpw);
		System.out.println("check : " + check);
		//$2a$12$MOBMt3yrNexubIK0U8751ObMvdNyPLmvGashS30feejaL76lZgo.y
		
		
	}
}
