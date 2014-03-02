package org.openmrs.module.dhisintegration.api.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.openmrs.module.dhisintegration.DhisApiResult;
import org.openmrs.module.dhisintegration.IntegrationServer;
import org.openmrs.module.dhisintegration.OrgUnit;
import org.openmrs.module.dhisintegration.OrgUnitDisplay;
import org.openmrs.module.dhisintegration.api.DhisApiService;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils;
import org.openmrs.module.dhisintegration.api.db.IntegrationException;
import org.openmrs.module.dhisintegration.api.db.ServerMetadata;
import org.openmrs.module.dhisintegration.api.db.DhisMetadataUtils.ContentType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class DhisApiServiceImpl implements DhisApiService {

	@Async
	@Override
	public Future<DhisApiResult> testConnection(IntegrationServer is) {
		DhisApiResult result = new DhisApiResult();
		result.setStatus(DhisMetadataUtils.testConnection(is));
		if ("".equals(result.getStatus())) {
			result.setStatus("integration.TestConnection.OK");
		} else {
			result.setError(true);
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> createServerMetadata(IntegrationServer is) {
		DhisApiResult result = new DhisApiResult();
		try {
			DhisMetadataUtils.getServerMetadata(is);
			DhisMetadataUtils.getDhisMetadataFromAPI(ContentType.ORGS,is);
		} catch (IntegrationException e) {
			result.setStatus(e.getMessage());
			result.setError(true);			
		}
		if (!result.getError()){
			return createServerMetadata(is.getServerName());
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> updateServerMetadata(IntegrationServer is) {
		DhisApiResult result = new DhisApiResult();
		try {
			DhisMetadataUtils.copyNewToCurrent(is.getServerName());
			DhisMetadataUtils.getServerMetadata(is);
		} catch (IntegrationException e) {
			result.setStatus(e.getMessage());
			result.setError(true);			
		}
		if (!result.getError()){
			return updateServerMetadata(is.getServerName());
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> updateServerOrgUnits(IntegrationServer is) {
		DhisApiResult result = new DhisApiResult();
		try {
			DhisMetadataUtils.getServerMetadata(is);
			DhisMetadataUtils.getDhisMetadataFromAPI(ContentType.ORGS,is);
		} catch (IntegrationException e) {
			result.setStatus(e.getMessage());
			result.setError(true);
		}
		if (!result.getError()){
			return updateServerOrgUnits(is.getServerName());
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> createServerMetadata(String server) {
		DhisApiResult result = new DhisApiResult();
		if (!result.getError()){
			ServerMetadata sm = new ServerMetadata();
			try {
				sm.buildDBObjects(server);
			} catch (IntegrationException e) {
				result.setStatus(e.getMessage());
				result.setError(true);
			} finally {
				sm = null;
			}
		}
		if (!result.getError()){
			try {
				DhisMetadataUtils.getOrgUnitDisplay(true, server);
			} catch (IntegrationException e) {
				result.setStatus(e.getMessage());
				result.setError(true);
			}
		}
		if (!result.getError()){
			result.setStatus("integration.CreateServerMetadata.OK");
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> updateServerMetadata(String server) {
		DhisApiResult result = new DhisApiResult();
		ServerMetadata sm = new ServerMetadata();
		try {
			result.setChanges(sm.updateServer(server));
		} catch (IntegrationException e) {
			result.setStatus(e.getMessage());
			result.setError(true);
		} finally {
			sm = null;
		}
		if (!result.getError()){
			result.setStatus("integration.UpdateServerMetadata.OK");
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> updateServerOrgUnits(String server) {
		DhisApiResult result = new DhisApiResult();
		try {
			DhisMetadataUtils.getOrgUnitDisplay(true, server);
			result.setRemoved(OrgUnitDisplay.findDeletedOrgs(server));
		} catch (IntegrationException e) {
			result.setStatus(e.getMessage());
			result.setError(true);
		} finally {
			OrgUnitDisplay.Reset();
		}
		if (!result.getError()){
			result.setStatus("integration.UpdateOrgUnits.OK");
		}
		return new AsyncResult<DhisApiResult>(result);
	}

	@Async
	@Override
	public Future<DhisApiResult> sendFileToServer(String server, String report, Date asOf, Date sent) {
		DhisApiResult result = new DhisApiResult();
		try {
			result.setSummary(DhisMetadataUtils.sendReportViaAPI(server, report, asOf, sent));
		} catch (IntegrationException e) {
			result.setStatus(e.getMessage());
			result.setError(true);
		}
		if (!result.getError()){
			result.setStatus("integration.SendFile.OK");
		}
		return new AsyncResult<DhisApiResult>(result);
	}

}
