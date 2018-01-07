package com.benefire.waldder.midpoint.core;

import java.util.function.Function;
import org.iq80.leveldb.*;

import com.google.common.base.Strings;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import java.io.*;

/**
 * @author JIANG
 */
public abstract class LeveldbTemplate {

	private final String dbName;

	public LeveldbTemplate(String dbName) {
		if (Strings.isNullOrEmpty(dbName))
			throw new IllegalArgumentException("[Assertion failed] - this argument is required; it must not be null.");
		this.dbName = dbName;
	}

	protected final <T> T execute(Function<DB, T> function) {

		return execute(null,function);
	}

	protected final <T> T execute(Options options, Function<DB, T> function) {
		DB db = null;
		try {
			options = null == options ? new Options() : options;
			db = factory.open(new File(dbName), options);
			return function.apply(db);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				if (null != db)
					db.close();
			} catch (IOException e) {
				throw new IllegalArgumentException("levedb closing the anomaly.", e);
			}
		}
	}

	protected byte[] serialize(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(obj);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				if (null != os)
					os.close();
				out.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return out.toByteArray();
	}

	@SuppressWarnings("unchecked")
	protected <T> T deserialize(byte[] data,Class<T> clazz) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(in);
			Object object = is.readObject();
			if (null != object) return (T) object;
		} catch (ClassNotFoundException | IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				if (null != is) is.close();
				in.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

}
