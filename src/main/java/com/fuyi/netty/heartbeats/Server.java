package com.fuyi.netty.heartbeats;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class Server {

	public static void main(String[] args) throws Throwable {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 512)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// 配置数据处理器
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new IdleStateHandler(5, 0, 0));
					pipeline.addLast("decoder", new StringDecoder());
					pipeline.addLast("encoder", new StringEncoder());
					pipeline.addLast(new HeartBeatServerHandler());
				}
			});
		
		System.out.println("Server start listen at " + 8765 );
		ChannelFuture channelFuture = serverBootstrap.bind(8765).sync();
		channelFuture.channel().closeFuture().sync();
		

		parentGroup.shutdownGracefully();
		childGroup.shutdownGracefully();
	}
	
}
