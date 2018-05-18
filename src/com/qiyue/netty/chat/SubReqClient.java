package com.qiyue.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SubReqClient {
	
	public void connect(String host,int port) throws Exception {
		EventLoopGroup group=new NioEventLoopGroup();
		try{
			Bootstrap bootstrap=new Bootstrap();
			bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				

				@Override
				protected void initChannel(SocketChannel arg0) throws Exception {
					arg0.pipeline().addLast(new ObjectDecoder(1024,ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					arg0.pipeline().addLast(new ObjectEncoder());
					arg0.pipeline().addLast(new SubReqClientHandler());
				}
			});
			ChannelFuture future=bootstrap.connect(host,port).sync();
			future.channel().closeFuture().sync();
			
		}finally {
			group.shutdownGracefully();
		}
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new SubReqClient().connect("127.0.0.1", 8080);
	}

}
