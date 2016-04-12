package com.banxian.util;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import com.banxian.entity.UserFormBean;

public class PasswordHelper {
	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
	private String algorithmName = "md5";
	private int hashIterations = 2;

	public void encryptPassword(UserFormBean userFormMap) {
		String salt=randomNumberGenerator.nextBytes().toHex();
		userFormMap.put("credentialsSalt", salt);
		String newPassword = new SimpleHash(algorithmName, userFormMap.get("password"), ByteSource.Util.bytes(userFormMap.get("accountName")+salt), hashIterations).toHex();
		userFormMap.put("password", newPassword); 
	}
	public static void main(String[] args) {
		PasswordHelper passwordHelper = new PasswordHelper();
		UserFormBean userFormMap = new UserFormBean();
		userFormMap.put("password","123456");
		userFormMap.put("accountName","admin");
		passwordHelper.encryptPassword(userFormMap);
		System.out.println(ByteSource.Util.bytes("admin4157c3feef4a6ed91b2c28cf4392f2d1"));
	}
}
