package com.qiyue.netty.chat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import javax.sound.midi.MidiDevice.Info;

public class TestUserInfo {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		UserInfo uInfo=new UserInfo();
		uInfo.buildUserId(100).buildUsername("Welcome to netty");
		int loop=1000000;
		ByteArrayOutputStream byteArrayOutputStream =null;
		ObjectOutputStream objectOutputStream=null;
		long starttime=System.currentTimeMillis();
		for(int i=0;i<loop;i++){
			byteArrayOutputStream =new ByteArrayOutputStream();
			objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(uInfo);
			objectOutputStream.flush();
			objectOutputStream.close();
			byte[] bos=byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
		}
		long endtime=System.currentTimeMillis();
		System.out.println("the jdk serialize id cost time  is "+(endtime-starttime)+" ms");
		System.out.println("------------------------------------------");
		ByteBuffer buffer=ByteBuffer.allocate(1024);
		starttime=System.currentTimeMillis();
		for(int i=0;i<loop;i++){
			byte[] bs=uInfo.codec(buffer);
		}
		endtime=System.currentTimeMillis();
		System.out.println("the byte array serialize id cost time  is "+(endtime-starttime)+" ms");
		/*objectOutputStream.writeObject(uInfo);
		objectOutputStream.flush();
		objectOutputStream.close();
		byte[] bos=byteArrayOutputStream.toByteArray();
		System.out.println("the jdk serialize id length is "+bos.length);
		byteArrayOutputStream.close();
		System.out.println("------------------------");
		System.out.println("the byte serialize id length is "+uInfo.codec().length);*/

	}

}
