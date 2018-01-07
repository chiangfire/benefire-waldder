package com.benefire.waldder.midpoint.core;

import static com.benefire.waldder.midpoint.domain.AbstractProperties.isInt;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;

import com.benefire.waldder.midpoint.Application;
import com.benefire.waldder.midpoint.core.support.LevelVertexRepository;
import com.benefire.waldder.midpoint.domain.Vertex;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
/**
 * @author JIANG
 */
public class KeepaliveChannelHandler extends AbstractHandlerAdapter{
	
	private static final VertexRepository vertexRepository = LevelVertexRepository.getInstance();
	
	public static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	public static final ConcurrentMap<Integer,ChannelId> KEEPALIVE_CHANNEL_ID_MAP = PlatformDependent.newConcurrentHashMap();
	public static final ConcurrentMap<Integer,KeepaliveChannelHandler> HANDLER_MAP = PlatformDependent.newConcurrentHashMap();
	private final SynchronousQueue<Channel> queue = new SynchronousQueue<>(true);
	//temporary use for running logic
	private Channel shortConnectionChannel = null;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf)msg;
		//Channel shortConnectionChannel = queue.poll();
		if(null == shortConnectionChannel && byteBuf.readableBytes() < 60){
			byte[] bytes = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(bytes);
		    String token = new String(bytes,Charsets.UTF_8);
		    String port = token.substring(token.lastIndexOf("-") + 1,token.length());
		    if(isInt(port)){
		    	int localPort = Integer.parseInt(port);
		    	Vertex vertex = vertexRepository.getVertex(Integer.parseInt(port) - 1);
		    	if(!KEEPALIVE_CHANNEL_ID_MAP.containsKey(localPort)&&null != vertex && token.equals(vertex.getToken())){
		    		CHANNEL_GROUP.add(ctx.channel());
		    		KEEPALIVE_CHANNEL_ID_MAP.put(localPort, ctx.channel().id());
		    		HANDLER_MAP.put(localPort, this);
		    		Application.boot.bind(localPort);
		    		return;
		    	}
		    }
		}
		if(null != shortConnectionChannel && shortConnectionChannel.isActive()){
			shortConnectionChannel.writeAndFlush(msg).addListener((future) ->{
				((ChannelFuture) future).channel().close();
				//handle next
				//queue.take();
			});
			return;
		}
		closeOnFlush(ctx.channel());
	}
	
	public void setShortConnectionChannel(Channel shortConnectionChannel) {
		this.shortConnectionChannel = shortConnectionChannel;
	}

	public SynchronousQueue<Channel> getQueue(){
		
		return queue;
	}

}
