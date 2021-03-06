package com.qiyue.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerHandler extends ChannelHandlerAdapter {

	int counter=0;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body=(String) msg;
		System.out.println("this is" + ++counter +" times receive client :["+body+"]");
		body+="$_";
		ByteBuf resp=Unpooled.copiedBuffer(body.getBytes());
		ctx.writeAndFlush(resp);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
