package com.minsait.onesait.architecture.multitenant.services;

import javax.servlet.http.HttpServletRequest;

public interface TenantProvider {	
	public String getTenant(HttpServletRequest request);
}
