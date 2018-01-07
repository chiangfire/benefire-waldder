package com.benefire.waldder.midpoint.domain;

import java.io.Serializable;


public class Vertex implements Serializable {
	
	private static final long serialVersionUID = 29375548889640938L;

	private String token;

	private int port;
	

	public Vertex(String token, int port) {
		super();
		this.token = token;
		this.port = port;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
