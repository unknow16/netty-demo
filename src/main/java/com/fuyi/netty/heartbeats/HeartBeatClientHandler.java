package com.fuyi.netty.heartbeats;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

public class HeartBeatClientHandler extends ChannelHandlerAdapter {
	
	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));
	
	private static final int TRY_TIMES = 3;
	
	private int currentTime;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("激活时间：" + new Date());
		System.out.println("HeartBeatClientHandler#channelActive");
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("停止时间：" + new Date());
		System.out.println("HeartBeatClientHandler#channelInactive");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println("循环触发时间：" + new Date());
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state() == IdleState.WRITER_IDLE) {
				if(currentTime < TRY_TIMES) {
					System.out.println("第" + currentTime + "次发送心跳");
					currentTime++;
					ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
				} else {
					System.out.println("Client停止发送心跳了");
				}
				
			}
		}

	}

}
