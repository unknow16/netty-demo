package com.fuyi.netty.attr;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

public class ClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ClientHandler#channelActive");
		Attribute<SessionInfo> attr = ctx.attr(AttributeMapConstant.SESSION_INFO_KEY);
		SessionInfo sessionInfo = attr.get();
		if(sessionInfo == null) {
			sessionInfo = new SessionInfo("123", "fuyi999999");
			sessionInfo = attr.setIfAbsent(sessionInfo);
			System.out.println("ClientHandler#channelActive()设置值");
		} else {
			System.out.println("ClientHandler#channelActive() # Attribute 有值: " + sessionInfo.getName());
		}
		
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("ClientHandler#channelRead");
		
		Attribute<SessionInfo> attr = ctx.attr(AttributeMapConstant.SESSION_INFO_KEY);
		SessionInfo sessionInfo = attr.get();
		if(sessionInfo == null) {
			sessionInfo = new SessionInfo("123", "fuyi");
			sessionInfo = attr.setIfAbsent(sessionInfo);
			System.out.println("ClientHandler#channelRead()设置值");
		} else {
			System.out.println("ClientHandler#channelRead() # Attribute 有值: " + sessionInfo.getName());
		}
		
		ctx.fireChannelRead(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
