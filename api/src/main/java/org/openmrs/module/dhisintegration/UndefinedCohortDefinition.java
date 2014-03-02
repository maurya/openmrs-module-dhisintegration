package org.openmrs.module.dhisintegration;

import java.util.Date;
import java.util.List;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;

/**
 * The undefined cohort definition is a singleton cohort definition with an empty cohort
 * The cohort definition is called INSTANCE_NAME
 */
public class UndefinedCohortDefinition extends BaseCohortDefinition {

	private static String MODULE_NAME = "Integration";
	private static String INSTANCE_NAME = "instance";
	private static String INSTANCE_DESC = "For use by " + MODULE_NAME;
	

	public UndefinedCohortDefinition() {
		super();
		super.setName(INSTANCE_NAME);
		super.setDescription(INSTANCE_DESC);
	}

}
