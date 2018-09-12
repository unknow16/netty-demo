package com.fuyi.netty.custom;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class CustomClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		CustomMsg customMsg = new CustomMsg((byte)0xAB, (byte)0xCD, "Hello,Netty".length(), "Hello,Netty");
        ctx.writeAndFlush(customMsg);
        
        System.out.println("Client 已发送消息： " + customMsg.getBody());
	}
}
