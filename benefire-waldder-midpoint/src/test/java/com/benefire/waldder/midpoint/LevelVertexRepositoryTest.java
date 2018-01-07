package com.benefire.waldder.midpoint;

import org.junit.Test;

import com.benefire.waldder.midpoint.core.support.LevelVertexRepository;
import com.benefire.waldder.midpoint.domain.Vertex;

public class LevelVertexRepositoryTest {
	
	@Test
	public void test(){
		int port = 8082;
		LevelVertexRepository lvr = LevelVertexRepository.getInstance();
		System.err.println(lvr.addVertex(port));
		Vertex vertex = lvr.getVertex(port);
		System.err.println(vertex.getToken());
		//lvr.deleteVertex(8082);
	}

}
