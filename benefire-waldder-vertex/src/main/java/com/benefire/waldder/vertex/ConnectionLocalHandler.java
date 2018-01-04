package com.benefire.waldder.vertex;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
/**
 * @author JIANG
 */
public class ConnectionLocalHandler extends ChannelInboundHandlerAdapter {
	
	public static final String DEFAULT_HOST = "127.0.0.1";
	private static final int LOCAL_CONNECT_TIMEOUT = 10000;
	
	
	private final InternalLogger LOG = InternalLoggerFactory.getInstance(getClass());

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel inboundChannel = ctx.channel();
		Bootstrap b = new Bootstrap();
		b.group(inboundChannel.eventLoop()).channel(ctx.channel().getClass())
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, LOCAL_CONNECT_TIMEOUT)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HexDumpRelayLocalHandler(inboundChannel, msg));
			}
		}).connect(DEFAULT_HOST, 8082);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		LOG.error("connection anomaly.",cause);
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

}
