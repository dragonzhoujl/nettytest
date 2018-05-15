package com.qiyue.netty.chat;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter{
	
	private int counter;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		////super.channelRead(ctx, msg);
		//ByteBuf buf=(ByteBuf) msg;
		String body=(String) msg;
		//byte[] req=new byte[buf.readableBytes()];
		//buf.readBytes(req);
		//String body=new String(req,"UTF-8").substring(0,req.length-System.getProperty("line.separator").length());
		System.out.println("the time server receivr order : "+body+" ; the counter is "+ ++counter);
		String currentTime="QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD QUERY ORDER";
		currentTime=currentTime+System.getProperty("line.separator");
		ByteBuf resp=Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		//super.exceptionCaught(ctx, cause);
		ctx.close();
	}

}
