package com.qiyue.netty.chat;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter {
	public SubReqClientHandler() {
		
	}

	public SubscribeReq getReq(int id)  {
		// TODO Auto-generated method stub
		SubscribeReq req=new SubscribeReq();
		req.setSubSeqID(id);
		req.setUserName("qiyue");
		req.setAddress("hz");
		req.setPhoneNumber("13666666666");
		req.setProductName("apple");
		return req;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i=1;i<=10;i++) {
			ctx.write(getReq(i));
		}
		ctx.flush();
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SubscribeResp resp=(SubscribeResp) msg;
		System.out.println("receive from server["+resp+"]");
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
