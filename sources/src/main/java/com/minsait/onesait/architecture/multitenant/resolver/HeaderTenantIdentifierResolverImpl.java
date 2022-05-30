package com.minsait.onesait.architecture.multitenant.resolver;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import com.minsait.onesait.architecture.multitenant.context.MultiTenantContext;

public class HeaderTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	private static String DEFAULT_TENANT_ID = "tenant_1";

	@Override
	public String resolveCurrentTenantIdentifier() {
		String currentTenantId = MultiTenantContext.getTenantId(); 
		return (currentTenantId != null) ? currentTenantId : DEFAULT_TENANT_ID;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}