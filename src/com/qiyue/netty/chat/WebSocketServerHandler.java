package com.qiyue.netty.chat;

import java.nio.charset.Charset;
import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
	
	private WebSocketServerHandshaker handshaker;
	

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if(msg instanceof FullHttpRequest){
			handleHttpRequest(ctx,(FullHttpRequest)msg);
		}
		if(msg instanceof WebSocketFrame){
			handleWebSocketFrame(ctx,(WebSocketFrame)msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if(frame instanceof CloseWebSocketFrame){//判断是否关闭链路的指令
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		if(frame instanceof PingWebSocketFrame){//判断是否ping消息
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if(!(frame instanceof TextWebSocketFrame)){//只支持文本消息
			throw new UnsupportedOperationException(String .format("%s frame types not supported ",frame.getClass().getName()));
			
		}
		//返回应答消息
		String request=((TextWebSocketFrame)frame).text();
		ctx.channel().write(new TextWebSocketFrame(request+",欢迎使用Netty WebSocket服务,现在时刻："+new Date(System.currentTimeMillis())));
		
		
		
	}


	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		if(!req.decoderResult().isSuccess()||!"websocket".equals(req.headers().get("Upgrade"))){
			sendHttpresponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		WebSocketServerHandshakerFactory factory=new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
		handshaker=factory.newHandshaker(req);
		if(handshaker==null){
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			
		}else{
			handshaker.handshake(ctx.channel(), req);
		}
		
	}

	private void sendHttpresponse(ChannelHandlerContext ctx, FullHttpRequest req,
			DefaultFullHttpResponse res) {
		if(res.status().code()!=200){
			ByteBuf  buf=Unpooled.copiedBuffer(res.status().toString(),CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			
		}
	}
	

}
