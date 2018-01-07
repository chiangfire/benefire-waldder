package com.benefire.waldder.vertex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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
	
	private final BasicPropertys basicPropertys;
	private static Application application;
	
	private Application() throws InterruptedException{
		this.basicPropertys = new BasicPropertys();
		initClient();
	}
	
	public Integer getLocalPort() {
		
		return basicPropertys.getLocalPort();
	}
	
	public Integer getLocalConnectTimeout() {
		
		return basicPropertys.getLocalConnectTimeout();
	}
	
	public void initClient() throws InterruptedException{
		new Thread(()->{
			NioEventLoopGroup group = new NioEventLoopGroup();
			try{
				Bootstrap boot = new Bootstrap();
				boot.option(ChannelOption.SO_KEEPALIVE, true).                                              
			    group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()/*.addLast(new CustomLoggingHandler(LogLevel.INFO))*/.addLast(new DispatcherLocalHandler());
					}
				});
				ChannelFuture sync = boot.connect(basicPropertys.getHost(),basicPropertys.getPort()).sync();
				sync.channel().writeAndFlush(Unpooled.copiedBuffer(basicPropertys.getKey().getBytes()));
				sync.channel().closeFuture().sync();
			}catch (Exception e) {
				throw new IllegalArgumentException(e);
			}finally{
				group.shutdownGracefully();
			}
		}).start();
	}
	

	
	public static void main(String[] args) throws InterruptedException {
		application = new Application();
	}
	
	
	private class BasicPropertys{
		
		private final String key;
		private final Integer port;
		private final String host;
		private final Integer localPort;
		private Integer localConnectTimeout = 1000;
		
		private BasicPropertys(){
			Properties properties = loadProperties();
			this.key = properties.getProperty("key");
			if(null == key || key.length() == 0){
				throw new IllegalArgumentException("not key.");
			}
			this.port = Integer.parseInt(key.substring(key.lastIndexOf("-") - 4,key.lastIndexOf("-")));
			this.host = parseIPv4(Long.valueOf(String.join("", key.split("-")[0],key.split("-")[2])));
			this.localPort = Integer.valueOf(key.substring(key.lastIndexOf("-") + 1,key.length())) -1;
			String property = properties.getProperty("local.connect.timeout");
			if(null != property && property.length() != 0){
				this.localConnectTimeout = Integer.valueOf(property);
			}
			
		}
		
		public Properties loadProperties(){
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
			try {
				Properties properties = new Properties();
				properties.load(inputStream);
				return properties;
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}finally{
				try {
					if(null != inputStream) inputStream.close();
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		private Integer getPort(){
			return port;
		}
		
		private String getKey() {
			return key;
		}
		
		private String getHost() {
			return host;
		}

		private Integer getLocalPort() {
			return localPort;
		}
		
		private Integer getLocalConnectTimeout() {
			return localConnectTimeout;
		}

		/**
		 * @see <a href="http://www.mkyong.com/java/java-convert-ip-address-to-decimal-number/">
		 *          http://www.mkyong.com/java/java-convert-ip-address-to-decimal-number/
		 *      </a>
		 * @param ipv4
		 * @return
		 */
		private String parseIPv4(long ipv4) {
			
			return String.join(".", String.valueOf(((ipv4 >> 24) & 0xFF)), String.valueOf(((ipv4 >> 16) & 0xFF)),
					String.valueOf(((ipv4 >> 8) & 0xFF)), String.valueOf((ipv4 & 0xFF)));
		}
	}
	
	public static final Application getInstance(){
		
		return application;
	}
	
}
