package com.fuyi.netty.reconnect.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

public class HeartBeatsClient {

	protected final HashedWheelTimer timer = new HashedWheelTimer();

	private Bootstrap bootstrap;
	
	private final ConnectionIdleStateTrigger idleStateTrigger = new ConnectionIdleStateTrigger();
	
	public void connect(String host, int port) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		bootstrap = new Bootstrap();
		bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO));
		
		final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, host, port, true) {
			
			public ChannelHandler[] handlers() {
				return new ChannelHandler[] {
						this, // 指ConnectionWatchdog
						new IdleStateHandler(0, 4, 0),
						idleStateTrigger,
						new StringDecoder(),
						new StringEncoder(),
						new BizClientHandler()
				};
			}
		};
		
		ChannelFuture future;
		
		//进行连接
		try {
			synchronized (bootstrap) {
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(watchdog.handlers());
					}
				});
				
				future = bootstrap.connect(host, port);
			}
			
			// 以下代码在synchronized同步块外面是安全的
            future.sync();
            future.channel().closeFuture().sync();
		} catch (Exception e) {
			throw new Exception("connect fail ", e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new HeartBeatsClient().connect("127.0.0.1", port);	
	}
}
