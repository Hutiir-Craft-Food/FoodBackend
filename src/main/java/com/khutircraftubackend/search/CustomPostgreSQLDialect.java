package com.khutircraftubackend.search;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class CustomPostgreSQLDialect extends PostgreSQLDialect {
	
	public CustomPostgreSQLDialect() {
		super();
	}
	
	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.contributeTypes(typeContributions, serviceRegistry);
		typeContributions.getTypeConfiguration()
				.getJdbcTypeRegistry()
				.addDescriptor(SqlTypes.VARCHAR, new CustomVarcharJdbcType());
		
	}
	
	private static class CustomVarcharJdbcType extends VarcharJdbcType {
		
		@Override
		public String getFriendlyName() {
			
			return "VARCHAR COLLATE \"uk_UA.UTF-8\"";
		}
	}
}
