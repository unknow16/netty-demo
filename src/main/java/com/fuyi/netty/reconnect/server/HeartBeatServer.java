package com.fuyi.netty.reconnect.server;

import java.net.InetSocketAddress;

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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class HeartBeatServer {

	private int port;
	
	private final AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();
	
	public HeartBeatServer(int port) {
		this.port = port;
	}
	
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup childGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, childGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.localAddress(new InetSocketAddress(this.port))
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new IdleStateHandler(5, 0, 0));
						pipeline.addLast(idleStateTrigger);
						pipeline.addLast("encoder", new StringEncoder());
						pipeline.addLast("decoder", new StringDecoder());
						pipeline.addLast(new BizServerHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.option(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture future = serverBootstrap.bind(this.port).sync();
			
			System.out.println("server listen in " + this.port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
	
	 public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new HeartBeatServer(port).start();
    }
}
