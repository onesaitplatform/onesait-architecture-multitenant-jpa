package com.minsait.onesait.architecture.multitenant.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Primary
@ConfigurationProperties(prefix = "architecture.multitenant-jpa")
public class OnesaitMultiTenantProperties {

	private List<DataSourceProperties> dataSourcesProps;

	public List<DataSourceProperties> getDataSources() {
		return this.dataSourcesProps;
	}

	public void setDataSources(List<DataSourceProperties> dataSourcesProps) {
		this.dataSourcesProps = dataSourcesProps;
	}

	public static class DataSourceProperties extends org.springframework.boot.autoconfigure.jdbc.DataSourceProperties {

		private String tenantId;

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}
	}
}