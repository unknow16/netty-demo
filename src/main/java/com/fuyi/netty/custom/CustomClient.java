package com.fuyi.netty.custom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CustomClient {
	
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8765;

	public static void main(String[] args) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new CustomEncoder());
							ch.pipeline().addLast(new CustomClientHandler());
						}
					});

			ChannelFuture future = b.connect(HOST, PORT).sync();
			future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
			future.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}

	}
}
