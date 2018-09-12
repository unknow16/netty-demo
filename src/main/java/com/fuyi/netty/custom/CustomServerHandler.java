package com.fuyi.netty.custom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CustomServerHandler extends SimpleChannelInboundHandler<CustomMsg> {

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, CustomMsg msg) throws Exception {
		System.out.println("Server收到Client消息： " + msg.getBody());
	}

}
