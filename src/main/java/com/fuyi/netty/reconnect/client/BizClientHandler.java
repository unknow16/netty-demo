package com.fuyi.netty.reconnect.client;

import java.util.Date;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class BizClientHandler extends ChannelHandlerAdapter {

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("激活时间是："+new Date());
        System.out.println("BizClientHandler channelActive");
        ctx.fireChannelActive();
    }
 
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("停止时间是："+new Date());
        System.out.println("BizClientHandler channelInactive");
        ctx.fireChannelInactive();
    }
 
 
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        System.out.println(message);
        if (message.equals("Heartbeat")) {
            ctx.write("has read message from server");
            ctx.flush();
        }
        ReferenceCountUtil.release(msg);
    }

}
