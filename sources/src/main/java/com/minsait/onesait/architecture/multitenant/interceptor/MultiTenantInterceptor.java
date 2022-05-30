package com.minsait.onesait.architecture.multitenant.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.minsait.onesait.architecture.multitenant.context.MultiTenantContext;
import com.minsait.onesait.architecture.multitenant.services.TenantProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MultiTenantInterceptor extends HandlerInterceptorAdapter  {


	private ApplicationContext _app;

	
	public MultiTenantInterceptor(ApplicationContext app) {
		this._app=app;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {		
		TenantProvider tp=(TenantProvider) _app.getBean("TenantProvider");		
		String tenant=tp.getTenant(request);
		if(tenant!=null) {
			MDC.put("tenantId", tenant);
			MultiTenantContext.setTenantId(tenant);
			
		}else
			log.info("No tenant loaded");
			
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		MultiTenantContext.clear();
	}

	
}