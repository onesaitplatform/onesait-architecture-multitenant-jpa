package com.minsait.onesait.architecture.multitenant.interceptor;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Resource
@Data
public class ApplicationContextProvider implements ApplicationContextAware {
	@Autowired
	private Config c;
	
	private static final ApplicationContextProvider INSTANCE = new ApplicationContextProvider();
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
		c=new Config(applicationContext);
		
	}
	@Bean
	public static ApplicationContextProvider getInstance() {
		return INSTANCE;
	}
}
