package com.minsait.onesait.architecture.multitenant.configuration.dto;

import lombok.Data;


@Data
public class TenantDTO {
	
	private Integer tenantDatasourceId;
	private Integer tenantId;
	private Integer moduleId;
	private String dbName;
	private String dbUser;
	private String dbPassword;
	
	
}
