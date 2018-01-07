package com.benefire.waldder.midpoint.core;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.benefire.waldder.midpoint.Application;
import com.benefire.waldder.midpoint.domain.RoutinePropertys;

public class IdGenerator {
	
	private final Random random;
	
	private static IdGenerator idGenerator = null;
	
	public final String LOCAL_ADDRESS = ipv4(Application.LOCAL_HOST_ADDRESS);
			
			
	private IdGenerator() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] seed = new byte[8];
		secureRandom.nextBytes(seed);
		this.random = new Random(new BigInteger(seed).longValue());
	}


	public String generateId() {
		byte[] randomBytes = new byte[16];
		this.random.nextBytes(randomBytes);

		long mostSigBits = 0;
		for (int i = 0; i < 8; i++) {
			mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
		}

		long leastSigBits = 0;
		for (int i = 8; i < 16; i++) {
			leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
		}
		StringBuilder id = new StringBuilder(LOCAL_ADDRESS.subSequence(0, 5)).append("-");
		id.append(new UUID(mostSigBits, leastSigBits).toString());
		id.insert(14, "-").insert(15, LOCAL_ADDRESS.substring(5));
		id.append(String.valueOf(RoutinePropertys.getInstance().getKeepalivePort()));
		return id.toString();
	}
	
	public String ipv4(String ipAddress) {
		long result = 0;
		String[] ipAddressInArray = ipAddress.split("\\.");
		for (int i = 3; i >= 0; i--) {
			long ip = Long.parseLong(ipAddressInArray[3 - i]);
			result |= ip << (i * 8);
		}
		return String.valueOf(result);
	}
	
	public static IdGenerator getInstance(){
		if(null == idGenerator){
			newInstance();
		}
		return idGenerator;
	}
	
	private static synchronized void newInstance(){
		if(null == idGenerator){
			idGenerator = new IdGenerator();
		}
	}
	
}
