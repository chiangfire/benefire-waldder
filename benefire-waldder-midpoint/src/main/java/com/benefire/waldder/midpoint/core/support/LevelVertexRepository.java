package com.benefire.waldder.midpoint.core.support;

import java.io.IOException;
import java.net.Socket;

import com.benefire.waldder.midpoint.Application;
import com.benefire.waldder.midpoint.core.IdGenerator;
import com.benefire.waldder.midpoint.core.LeveldbTemplate;
import com.benefire.waldder.midpoint.core.VertexRepository;
import com.benefire.waldder.midpoint.domain.Vertex;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;


/**
 * @author JIANG
 */
public class LevelVertexRepository extends LeveldbTemplate implements VertexRepository {
	
	private static final LevelVertexRepository levelVertexRepository = new LevelVertexRepository("vertex");

	private LevelVertexRepository(String dbName) {
		super(dbName);
	}

	@Override
	public synchronized String addVertex(int port) {
		if (port != 0) {
			if (isPortUsing(port))
				return "ERROR: port has been occupied.";
			if (null != getVertex(port))
				return "ERROR: port has been used.";
			return execute((db) -> {
				int increasing = increasing(port);
				String token = String.join("-", IdGenerator.getInstance().generateId(),String.valueOf(increasing));
				db.put(bytes(String.valueOf(increasing)), serialize(new Vertex(token, increasing)));
				return String.join(" ", "SUCCESS:",token);
			});
		}
		return "WARN: please enter the port.";
	}

	@Override
	public Vertex getVertex(int port) {
		if (0 != port) {
			return execute((db) -> {
				int increasing = increasing(port);
				byte[] bs = db.get(bytes(String.valueOf(increasing)));
				if (null != bs && bs.length != 0) {
					return deserialize(bs, Vertex.class);
				}
				return null;
			});
		}
		return null;
	}
	
	public void deleteVertex(int ... ports){
		if(null != ports){
			execute((db) -> {
				for(int port:ports){
					db.delete(bytes(String.valueOf(increasing(port))));
				}
				return null;
			});
		}
	}

	private boolean isPortUsing(int port) {
		Socket socket = null;
		try {
			socket = new Socket(Application.LOCAL_HOST_ADDRESS,increasing(port));
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (null != socket) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				return true;
			}
		}
		return true;
	}
	
	private int increasing(int port){
		
		return ++port;
	}
	
	public static final LevelVertexRepository getInstance(){
		
		return levelVertexRepository;
	}

}
