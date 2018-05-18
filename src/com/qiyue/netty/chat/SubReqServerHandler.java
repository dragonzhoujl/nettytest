package com.qiyue.netty.chat;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SubscribeReq req=(SubscribeReq) msg;
		if("QiYue".equalsIgnoreCase(req.getUserName())) {
			System.out.println("Server accept client["+req+"]");
			ctx.writeAndFlush(resp(req.getSubSeqID()));
		}
	}

	private SubscribeResp resp(int subSeqID) {
		// TODO Auto-generated method stub
		SubscribeResp resp=new SubscribeResp();
		resp.setSubSeqID(subSeqID);
		resp.setDesc("netty book order succeed");
		resp.setRespCode(0);
		return resp;
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
