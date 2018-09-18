package com.fuyi.netty.attr;

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

public class Client {

	public static void main(String[] args) {
		new Client("127.0.0.1", 9999);
	}
	
	public Client(String host, int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					
					pipeline.addLast("encoder", new StringEncoder());
					pipeline.addLast("decoder", new StringDecoder());
					pipeline.addLast(new ClientHandler());
					pipeline.addLast(new Client2Handler());
				}
			});
		try {
			ChannelFuture future = bootstrap.connect(host, port).sync();
			
			// 发送消息
			future.channel().writeAndFlush("Hello Netty");
			
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
			
	}
}