package com.benefire.waldder.midpoint.core;

import com.benefire.waldder.midpoint.domain.Vertex;

public interface VertexRepository {
	
	public String addVertex(int port);
	
	public Vertex getVertex(int port);
	
	public void deleteVertex(int ... ports);
	
}
