package com.qiyue.netty.chat;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	
	private int userId;
	
	public UserInfo buildUsername(String username){
		this.userName=username;
		return this;
	}
	public UserInfo buildUserId(int id){
		this.userId=id;
		return this;
	}

	public final String getUserName() {
		return userName;
	}

	public final void setUserName(String userName) {
		this.userName = userName;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(int userId) {
		this.userId = userId;
	}
	
	public byte[] codec(){
		ByteBuffer buffer=ByteBuffer.allocate(1024) ;
		byte[] value=this.userName.getBytes();
		buffer.putInt(value.length);
		buffer.put(value);
		buffer.putInt(userId);
		buffer.flip();
		value=null;
		byte[] result=new byte[buffer.remaining()];
		buffer.get(result);
		return result;
		
	}
	

}
