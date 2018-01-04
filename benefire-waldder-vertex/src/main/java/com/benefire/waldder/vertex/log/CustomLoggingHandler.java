package com.benefire.waldder.vertex.log;


import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jiang
 */
public class CustomLoggingHandler extends LoggingHandler {
	
	public CustomLoggingHandler(LogLevel level){
		super(level);
	}
	
}
