package com.qiyue.netty.chat;

import java.awt.Event;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

	private static final String DEAFULT_URL="/src/com/qiyue/netty/";
	
	public void run(final int port,final String url) throws Exception {
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try{
			ServerBootstrap bootstrap=new ServerBootstrap();
			bootstrap.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel arg0) throws Exception {
					arg0.pipeline().addLast("http-decoder",new HttpRequestDecoder());
					arg0.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
					arg0.pipeline().addLast("http-encoder",new HttpResponseEncoder());
					arg0.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
					arg0.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(url));
					
				}
			});
			ChannelFuture future=bootstrap.bind(port).sync();
			System.out.println("文件目录服务器启动，网址是： "+"http://192.168.1.102:"+port+url);
			future.channel().closeFuture().sync();
			
			
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
		
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new HttpFileServer().run(8080, DEAFULT_URL);
	}

}
