package com.fuyi.netty.hello;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client channelActive...");
		
		// 发送消息
		//channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("ping".getBytes()));
//		StringBuffer sb = new StringBuffer();
//		for(int i=0; i<50; i++) {
//			sb.append("hello");
//		}
		
		//channelFuture.channel().writeAndFlush(sb.toString());
				
		
		/*byte[] req = ("In this chapter you general, &&__we recommend Java Concurrency in Practice by Brian Goetz. His book w"
                + "ill give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the process "
                + "of configuring and connecting all of Netty’s components to bring your learned about threading models in ge"
                + "neral and Netty’s threading model in particular, whose performance and consistency advantages we discuss"
                + "ed in detail In this chapter you general, &&__we recommend Java Concurrency in Practice by Brian Goetz. Hi"
                + "s book will give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the"
                + " process of configuring and connecting all of Netty’s components to bring your learned about threading "
                + "models in general and Netty’s threading model in particular, &&__whose performance and consistency advantag"
                + "es we discussed in detailIn this chapter you general, we recommend Java Concurrency in Practice by Bri"
                + "an Goetz. &&__His book will give We’ve reached an exciting point—in the next chapter;the counter is: 1 2222"
                + "sdsa ddasd asdsadas dsadasdas" + System.getProperty("line.separator")).getBytes();
		
		ctx.channel().writeAndFlush(Unpooled.copiedBuffer(req));
		ctx.channel().writeAndFlush(Unpooled.copiedBuffer(req));*/
		
/*		byte[] req = ("BazingaLyncc is learner").getBytes();
		for(int i=0; i<100; i++) {
			ctx.channel().writeAndFlush(Unpooled.copiedBuffer(req));
		}*/
		
		String req = "ping";
		
		Thread.sleep(4000);
		ctx.channel().writeAndFlush(req);
		Thread.sleep(4000);
		ctx.channel().writeAndFlush(req);
		Thread.sleep(4000);
		ctx.channel().writeAndFlush(req);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf resp = (ByteBuf) msg;
			
			byte[] respByte = new byte[resp.readableBytes()];
			resp.readBytes(respByte);
			
			String str = new String(respByte, "UTF-8");
			System.out.println("Client 收到响应 ： " + str);
		} else if (msg instanceof String) {
			System.out.println("Client 收到响应 ： " + msg.toString());
		}
		
		ctx.fireChannelRead(msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client channelReadComplete");
	}
	
}
