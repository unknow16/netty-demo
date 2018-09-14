package com.fuyi.netty.hello;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server channel active ...");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf reqData = (ByteBuf)msg;
			
			byte[] req = new byte[reqData.readableBytes()];
			reqData.readBytes(req);
			
			String str = new String(req, "UTF-8");
			System.out.println("Server 收到  Client的消息：" + str);
			
			
			// 响应Client
			//ctx.channel().writeAndFlush(Unpooled.copiedBuffer("pong".getBytes()));
		} else if (msg instanceof String) {
			System.out.println("Server 收到  Client 的消息：" + msg.toString());
			
			//ctx.channel().writeAndFlush("pong");
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server channelReadComplete");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println("userEventTriggered...");
	}
	
}
