package com.fuyi.netty.heartbeats;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class Client {


	public static void main(String[] args) throws Throwable {
		EventLoopGroup group = new NioEventLoopGroup();
		ChannelFuture channelFuture = null;
		
		Bootstrap bootstrap = new Bootstrap();
		try {
			bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new IdleStateHandler(0, 4, 0));
						pipeline.addLast("decoder", new StringDecoder());
						pipeline.addLast("encoder", new StringEncoder());
						pipeline.addLast(new HeartBeatClientHandler());
					}
				});
			
			channelFuture = bootstrap.connect("127.0.0.1", 8765).sync();
			channelFuture.channel().closeFuture().sync();
		} finally {
			if(null != channelFuture) {
				if(channelFuture.channel() != null && channelFuture.channel().isOpen()) {
					channelFuture.channel().close();
				}
			}
			
			group.shutdownGracefully();
		}
	}
}
