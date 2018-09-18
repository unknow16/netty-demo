package com.fuyi.netty.attr;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

public class Client2Handler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client2Handler#channelActive");
		Attribute<SessionInfo> attr = ctx.attr(AttributeMapConstant.SESSION2_INFO_KEY);
		SessionInfo sessionInfo = attr.get();
		if(sessionInfo == null) {
			sessionInfo = new SessionInfo("123", "fuyi2222222");
			sessionInfo = attr.setIfAbsent(sessionInfo);
			System.out.println("Client2Handler#channelActive()设置值");
		} else {
			System.out.println("Client2Handler#channelActive() # Attribute 有值： " + sessionInfo.getName());
		}
		
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Client2Handler#channelRead");
		Attribute<SessionInfo> attr = ctx.attr(AttributeMapConstant.SESSION2_INFO_KEY);
		SessionInfo sessionInfo = attr.get();
		if(sessionInfo == null) {
			sessionInfo = new SessionInfo("123", "fuyi");
			sessionInfo = attr.setIfAbsent(sessionInfo);
			System.out.println("Client2Handler#channelActive()设置值");
		} else {
			System.out.println("Client2Handler#channelRead() # Attribute 有值： " + sessionInfo.getName());
		}
		
		ctx.fireChannelRead(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
