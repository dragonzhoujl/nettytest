package com.qiyue.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	
	public void run(int port) throws Exception{
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try{
			ServerBootstrap bootstrap=new ServerBootstrap();
			bootstrap.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel arg0) throws Exception {
					ChannelPipeline pipeline= arg0.pipeline();
					pipeline.addLast("http-codec",new HttpServerCodec());
					pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
					pipeline.addLast("http-chunked",new ChunkedWriteHandler());
					pipeline.addLast(new WebSocketServerHandler());
					
				}
				
			});
			Channel channel=bootstrap.bind(port).sync().channel();
			System.out.println("Web Socket Server started at port:"+port);
			System.out.println("Open your browse  and navigate to http://localhost:"+port+"/");
			channel.closeFuture().sync();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new WebSocketServer().run(8080);

	}

}
