package com.fuyi.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {

	public static void main(String[] args) throws Throwable {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 512)
			//.option(ChannelOption.SO_SNDBUF, 512)
			//.option(ChannelOption.SO_RCVBUF, 512)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// 配置数据处理器
					ChannelPipeline pipeline = ch.pipeline();
					//pipeline.addLast("decoder", new StringDecoder());
					//pipeline.addLast("encoder", new StringEncoder());
					//pipeline.addLast(new LineBasedFrameDecoder(1024));
					//pipeline.addLast(new FixedLengthFrameDecoder(23));
					pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("&&__".getBytes())));
					pipeline.addLast(new ServerHandler());
				}
			});
		
		ChannelFuture channelFuture = serverBootstrap.bind(8765).sync();
		channelFuture.channel().closeFuture().sync();
		
		System.out.println("Server start listen at " + 8765 );

		parentGroup.shutdownGracefully();
		childGroup.shutdownGracefully();
	}
	
}
