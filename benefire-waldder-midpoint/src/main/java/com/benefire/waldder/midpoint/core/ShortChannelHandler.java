package com.benefire.waldder.midpoint.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

/**
 * @author JIANG
 */
public class ShortChannelHandler extends AbstractHandlerAdapter {

	private final int shortConnectionPort;
	private Channel keepaliveConnectionChannel = null;

	public ShortChannelHandler(int shortConnectionPort) {
		this.shortConnectionPort = shortConnectionPort;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ByteBuf) {
			Channel inboundChannel = ctx.channel();
			if (null == keepaliveConnectionChannel || !keepaliveConnectionChannel.isActive()) {
				ChannelId channelId = KeepaliveChannelHandler.KEEPALIVE_CHANNEL_ID_MAP.get(shortConnectionPort);
				if (null != channelId) {
					keepaliveConnectionChannel = KeepaliveChannelHandler.CHANNEL_GROUP.find(channelId);
					KeepaliveChannelHandler.HANDLER_MAP.get(shortConnectionPort).setShortConnectionChannel(inboundChannel);
				}
			}
			if (null != keepaliveConnectionChannel && keepaliveConnectionChannel.isActive()) {
				keepaliveConnectionChannel.writeAndFlush(msg);
				return;

			}
		}
		closeOnFlush(ctx.channel());
	}
}
