package com.minsait.onesait.architecture.multitenant.services.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.minsait.onesait.architecture.multitenant.services.TenantProvider;

@Component("TenantProvider")
@ConditionalOnProperty(prefix = "architecture.multitenant-jpa", name = "tenantProvider", havingValue = "HEADER")
public class TenantProviderHeader implements TenantProvider{
	
	@Value("${architecture.multitenant-jpa.tenantField:X-TENANT-ID}")
	String tenantField;
	
	@Override
	public String getTenant(HttpServletRequest request) {		
		return request.getHeader(tenantField);
		
	}

}
