package com.benefire.waldder.midpoint.domain;

import com.benefire.waldder.midpoint.annotation.Value;

/**
 * @author JIANG
 */
public class RoutinePropertys implements AbstractProperties{
	
	private static final String fileName = "application.properties";
	
	private static RoutinePropertys routinePropertys = null;
	
	@Value("channel.option.so_backlog")
	private Integer soBacklog = 1024;
	@Value("channel.option.so_sndbuf")
	private Integer soSndbuf = 1024;
	@Value("channel.option.so_rcvbuf")
	private Integer soRcvbuf = 1024;
	@Value("channel.option.keepalive_port")
	private Integer keepalivePort = 9876;
	@Value("channel.wait.timeout")
	private Integer waitTimeout = 60;
	
	private RoutinePropertys() {
		initProperties(fileName);
	}
	
	public Integer getSoBacklog() {
		return soBacklog;
	}

	public void setSoBacklog(Integer soBacklog) {
		this.soBacklog = soBacklog;
	}

	public Integer getSoSndbuf() {
		return soSndbuf;
	}

	public void setSoSndbuf(Integer soSndbuf) {
		this.soSndbuf = soSndbuf;
	}

	public Integer getSoRcvbuf() {
		return soRcvbuf;
	}

	public void setSoRcvbuf(Integer soRcvbuf) {
		this.soRcvbuf = soRcvbuf;
	}

	public Integer getKeepalivePort() {
		return keepalivePort;
	}

	public void setKeepalivePort(Integer keepalivePort) {
		this.keepalivePort = keepalivePort;
	}

	public Integer getWaitTimeout() {
		return waitTimeout;
	}

	public void setWaitTimeout(Integer waitTimeout) {
		this.waitTimeout = waitTimeout;
	}
	
	public static final RoutinePropertys getInstance(){
		if(null == routinePropertys){
			newInstance();
		}
		return routinePropertys;
		
	}
	private synchronized static final void newInstance(){
		if(null == routinePropertys){
			routinePropertys = new RoutinePropertys();
		}
	}
	
}
