package com.qiyue.netty.chat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestUserInfo {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		UserInfo uInfo=new UserInfo();
		uInfo.buildUserId(100).buildUsername("Welcome to netty");
		ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(uInfo);
		objectOutputStream.flush();
		objectOutputStream.close();
		byte[] bos=byteArrayOutputStream.toByteArray();
		System.out.println("the jdk serialize id length is "+bos.length);
		byteArrayOutputStream.close();
		System.out.println("------------------------");
		System.out.println("the byte serialize id length is "+uInfo.codec().length);

	}

}
