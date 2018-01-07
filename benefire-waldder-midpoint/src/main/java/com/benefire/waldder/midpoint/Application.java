package com.benefire.waldder.midpoint;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.benefire.waldder.midpoint.core.KeepaliveChannelHandler;
import com.benefire.waldder.midpoint.core.ShortChannelHandler;
import com.benefire.waldder.midpoint.domain.RoutinePropertys;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/**
 * @author JIANG
 */
public class Application {

	private final DispatcherChannelHandler dispatcherChannelHandler = new DispatcherChannelHandler();
	public static ServerBootstrap boot = new ServerBootstrap();
	public static final String LOCAL_HOST_ADDRESS = getHostAddress();
	public RoutinePropertys routinePropertys = RoutinePropertys.getInstance();
	
	private Application() throws InterruptedException{
		initServer();
	}

	public static void main(String[] args) throws InterruptedException {
		new Application();
	}
	
	private void initServer() throws InterruptedException{
		NioEventLoopGroup pGroup = new NioEventLoopGroup();
		NioEventLoopGroup cGroup = new NioEventLoopGroup();
		try {
			boot.group(pGroup, cGroup).channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.option(ChannelOption.SO_BACKLOG, routinePropertys.getSoBacklog())
			.option(ChannelOption.SO_SNDBUF, 32 * routinePropertys.getSoSndbuf())
			.option(ChannelOption.SO_RCVBUF, 32 * routinePropertys.getSoRcvbuf())
			.option(ChannelOption.SO_KEEPALIVE, true)
			//.childOption(ChannelOption.AUTO_READ, false)
			.childHandler(dispatcherChannelHandler)
			.bind(routinePropertys.getKeepalivePort()).sync().channel().closeFuture().sync();
		} finally {
			pGroup.shutdownGracefully();
			cGroup.shutdownGracefully();
		}
	}
	
	private class DispatcherChannelHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			Integer port = ((InetSocketAddress) ch.localAddress()).getPort();
			if (port.equals(routinePropertys.getKeepalivePort())) {
				ch.pipeline().addLast(new KeepaliveChannelHandler());
			} else {
				ch.pipeline().addLast(new ShortChannelHandler(port));
			}
		}
	}
	
	
	private static final String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
