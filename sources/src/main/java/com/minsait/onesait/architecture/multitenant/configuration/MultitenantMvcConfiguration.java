package com.minsait.onesait.architecture.multitenant.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.minsait.onesait.architecture.multitenant.interceptor.MultiTenantInterceptor;

@Configuration
public class MultitenantMvcConfiguration implements WebMvcConfigurer {

	@Autowired
	private ApplicationContext app;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new MultiTenantInterceptor(app));
	}
}