package org.openmrs.module.dhisintegration;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.cohort.definition.evaluator.EncounterCohortDefinitionEvaluator;


/**
 * The service location cohort definition is a singleton encounter cohort definition with a
 * parameterized list of locations called and startDate and endDate
 * It is used by the DHIS2 Integration module as its primary selector
 */
public class ServiceLocationCohortDefinitionEvaluator extends EncounterCohortDefinitionEvaluator {

}
