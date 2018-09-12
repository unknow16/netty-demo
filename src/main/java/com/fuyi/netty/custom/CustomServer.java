package com.fuyi.netty.custom;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CustomServer {

	private int port;
	private static final int maxFrameLength = 1024 * 1024;
	private static final int lengthFieldOffset = 2;
	private static final int lengthFieldLength = 4;
	private static final int lengthAdjustment = 0;
	private static final int initialBytesToStrip = 0;
	
	
	public static void main(String[] args) {
		new CustomServer(8765).start();
	}
	
	public CustomServer(int port) {
		this.port = port;
	}
	
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		try {
			
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(this.port))
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new CustomDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, false));
						ch.pipeline().addLast(new CustomServerHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			// 绑定端口监听
			ChannelFuture future = sb.bind(this.port).sync();
			System.out.println("Server start listen at " + this.port );
			
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}
