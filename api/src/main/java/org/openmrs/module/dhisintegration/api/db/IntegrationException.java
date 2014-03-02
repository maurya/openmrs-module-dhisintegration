package org.openmrs.module.dhisintegration.api.db;

public class IntegrationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public IntegrationException() {
		super();
	}
	
	public IntegrationException(String message) {
		super(message);
	}
	
	public IntegrationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public IntegrationException(Throwable cause) {
		super(cause);
	}
}
