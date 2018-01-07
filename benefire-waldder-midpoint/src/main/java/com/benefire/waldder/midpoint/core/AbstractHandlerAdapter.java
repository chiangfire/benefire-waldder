package com.benefire.waldder.midpoint.core;


import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
/**
 * @author JIANG
 */
public abstract class AbstractHandlerAdapter extends ChannelInboundHandlerAdapter{
	
	protected final InternalLogger LOG = InternalLoggerFactory.getInstance(getClass());
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("connection anomaly",cause);
        closeOnFlush(ctx.channel());
	}
	
	
	
    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    protected static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
    
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception { 
//        //channelGroup.add(ctx.channel());
//    }
    
}
