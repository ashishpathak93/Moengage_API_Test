package com.moengage.apitest.log;

import org.apache.log4j.xml.DOMConfigurator;

public class AutoLogger {
	
	static CustomLogger logger;
	
	public static CustomLogger getAutoLogger(){

		if(logger == null){
			logger = new CustomLogger(CustomLogger.class);
			
			DOMConfigurator.configure("Log4j.xml");			
		}
		
		return logger;
	}

}
