package com.fuyi.netty.heartbeats;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelHandlerAdapter {

	private int lossTime;
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state() == IdleState.READER_IDLE) {
				lossTime ++;
				System.out.println("已经5秒内没收到客户端心跳了");
				if(lossTime > 2) {
					System.out.println("超过2次没收到客户端心跳，进行关闭客户端连接");
					ctx.channel().close();
				}
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server channel read...");
		System.out.println("client " + ctx.channel().remoteAddress() + " -> Server ： " + msg.toString());
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
