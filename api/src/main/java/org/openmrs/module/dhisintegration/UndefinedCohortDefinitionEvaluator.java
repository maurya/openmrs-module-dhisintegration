package org.openmrs.module.dhisintegration;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * The undefined cohort definition is a singleton cohort definition with an empty cohort
 * It is used by the DHIS2 Integration module as a placeholder
 * The empty cohort allows reports to run but not produce any results
 */
public class UndefinedCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition,
			EvaluationContext context) throws EvaluationException {
			return new EvaluatedCohort(null,cohortDefinition,context);
	}

}
