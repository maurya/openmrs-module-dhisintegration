package org.openmrs.module.dhisintegration.api;

import java.util.Date;
import java.util.concurrent.Future;

import org.openmrs.module.dhisintegration.DhisApiResult;
import org.openmrs.module.dhisintegration.IntegrationServer;

public interface DhisApiService {
	
	public Future<DhisApiResult> testConnection(IntegrationServer is);
	
	public Future<DhisApiResult> createServerMetadata(IntegrationServer is);
	
	public Future<DhisApiResult> updateServerMetadata(IntegrationServer is);
	
	public Future<DhisApiResult> updateServerOrgUnits(IntegrationServer is);
	
	public Future<DhisApiResult> createServerMetadata(String server);
	
	public Future<DhisApiResult> updateServerMetadata(String server);
	
	public Future<DhisApiResult> updateServerOrgUnits(String server);
	
	public Future<DhisApiResult> sendFileToServer(String server, String report, Date asOf, Date sent);

}
;