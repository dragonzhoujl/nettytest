package com.qiyue.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoDCServer {
	
	public void bind(int port) throws Exception{
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try{
			
			ServerBootstrap bootstrap=new ServerBootstrap();
			bootstrap.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel arg0) throws Exception {
					// TODO Auto-generated method stub
					//ByteBuf buf=Unpooled.copiedBuffer("$_".getBytes());
					//arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
					arg0.pipeline().addLast(new FixedLengthFrameDecoder(20));
					arg0.pipeline().addLast(new StringDecoder());
					arg0.pipeline().addLast(new EchoDCServerHandler());
					
					
				}
				
			});
			ChannelFuture future=bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new EchoDCServer().bind(8080);
	}

}
