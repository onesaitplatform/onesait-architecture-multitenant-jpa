package com.minsait.onesait.architecture.multitenant.interceptor;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Config {

	private static ApplicationContext applicationContext = null;
	
	public Config(ApplicationContext a){
		Config.applicationContext=a;
	}
	
	public ApplicationContext getApplicationContext() {
		return Config.applicationContext;
	}
	
}
