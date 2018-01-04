package com.benefire.waldder.vertex;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author JIANG
 */
public class Application {
	
	static final int DEFAULT_PORT = 8765;
	
	public static void main(String[] args) throws InterruptedException {
		
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap boot = new Bootstrap();
		boot.option(ChannelOption.SO_KEEPALIVE, true).                                              
	    group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline()/*.addLast(new CustomLoggingHandler(LogLevel.INFO))*/.addLast(new ConnectionLocalHandler());
			}
		});
		ChannelFuture sync = boot.connect(ConnectionLocalHandler.DEFAULT_HOST,DEFAULT_PORT).sync();
		sync.channel().closeFuture().sync();
		group.shutdownGracefully();
	}
}
