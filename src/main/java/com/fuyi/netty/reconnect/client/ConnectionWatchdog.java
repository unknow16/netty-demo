package com.fuyi.netty.reconnect.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;


/**
 * 
 * 重连检测狗，当发现当前的链路不稳定关闭之后，进行12次重连
 */
@Sharable
public abstract class ConnectionWatchdog extends ChannelHandlerAdapter implements TimerTask, ChannelHandlerHolder {

	private final Bootstrap bootstrap;
    private final Timer timer;
    private final String host;
    private final int port;
    
 
    private volatile boolean reconnect = true;
    private int attempts;
    
    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, String host, int port, boolean reconnect) {
    	this.bootstrap = bootstrap;
    	this.timer = timer;
    	this.host = host;
    	this.port = port;
    	this.reconnect = reconnect;
    }
    
    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("当前链路已经激活了，重连尝试次数重新置为0");
        
        attempts = 0;
        ctx.fireChannelActive();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("链路关闭");
    	if(reconnect) {
    		System.out.println("链路关闭，将进行重连");
    		if(attempts < 12) {
    			attempts++;
    			
    			int timeout = 2 << attempts;
    			timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
    		} else {
    			System.out.println("重连超过12次， 客户端将不再重连");
    		}
    	}
    	ctx.fireChannelInactive();
    }
    

	public void run(Timeout timeout) throws Exception {
		ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
		synchronized (bootstrap) {
			bootstrap.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(handlers());
				}	
			});
			future = bootstrap.connect(host, port);
		}
		
		future.addListener(new ChannelFutureListener() {
			
			public void operationComplete(ChannelFuture future) throws Exception {
				boolean successed = future.isSuccess();
				
				//如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
				if(!successed) {
					System.out.println("重连失败, 已尝试 " + attempts + "次");
					future.channel().pipeline().fireChannelInactive();
				} else {
					System.out.println("重连成功");
				}
			}
		});
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    	ctx.fireExceptionCaught(cause);
    }
}
