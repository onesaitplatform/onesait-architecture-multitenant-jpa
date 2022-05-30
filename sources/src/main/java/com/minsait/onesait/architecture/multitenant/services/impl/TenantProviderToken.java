package com.minsait.onesait.architecture.multitenant.services.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.minsait.onesait.architecture.multitenant.services.TenantProvider;

@Component("TenantProvider")
@ConditionalOnProperty(prefix = "architecture.multitenant-jpa", name = "tenantProvider", havingValue = "TOKEN",matchIfMissing = true)
public class TenantProviderToken implements TenantProvider{

	@Value("${architecture.multitenant-jpa.tenantField:tenant}")
	String tenantField;
	
	@Override
	public String getTenant(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if(authorization==null || authorization.isEmpty())
			return null;
		try {
		    DecodedJWT jwt = JWT.decode(authorization.replaceAll("Bearer ", ""));
		    Claim claim = jwt.getClaim(tenantField);
		    return claim.asString();
		} catch (JWTDecodeException exception){
		    //Invalid token
		}		
		return null;
	}

}
