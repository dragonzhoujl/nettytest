package com.qiyue.netty.chat;

import java.io.Serializable;

public class SubscribeResp implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int subSeqID;
	
	private int respCode;
	
	private String desc;

	public int getSubSeqID() {
		return subSeqID;
	}

	public void setSubSeqID(int subSeqID) {
		this.subSeqID = subSeqID;
	}

	public int getRespCode() {
		return respCode;
	}

	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return "SuscribeResp [ subSeqID="+subSeqID+",respcode="+respCode+",desc="+desc+"]";
	}

}
