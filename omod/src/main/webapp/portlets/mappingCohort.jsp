<%@ include file="/WEB-INF/template/include.jsp"%>
<script type="text/javascript" charset="utf-8">

	var win;	// child cohort def window

	$j(document).ready(function() {
		
		$j("#btnNewCohort").click( function() {
			if (win) {
				win.open();
			} else {
				win = window.open("/openmrs/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition","_blank");
			}
			win.focus();
		})

	
	});
</script>
<div>
	<div>
		<form method="post" id="detailsedit">
			<table>
				<tbody>
					<tr>
						<td>${model.type}</td>
						<td>:</td>
						<td><input id="id${model.portletId}" type="hidden"
							value="${model.portletId}" /> <input
							id="${model.type}Name${model.portletId}" type="text" size="40" /></td>
					</tr>
					<tr>
						<td><spring:message code="integration.general.mappedTo" /></td>
						<td>:</td>
						<td><select name='cohorts${model.portletId}'
							id='cohorts${model.portletId}'>
								<option value=""></option>
								<c:forEach items="${model.cohorts}" var="cohort">
									<c:set var="uuid" value="${cohort.uuid}" />
									<c:set var="mappedValue" value="${model.mappedCohort}" />
									<c:choose>
										<c:when test="${mappedValue == uuid}">
											<option id="${cohort.uuid}" value="${cohort.uuid}"
												selected="selected">${cohort}</option>
										</c:when>
										<c:otherwise>
											<option id="${cohort.uuid}" value="${cohort.uuid}">${cohort}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
						</select></td>
						<td>
							<button id="btnNewCohort">
								<spring:message code="integration.button.createNewCohort" />
							</button>
						</td>
					</tr>


					<tr>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td><a
							href="javascript:save${model.type}(${model.portletId});"><input
								id="${model.portletId}" type="button"
								value='<spring:message code="integration.button.save"/>' /> </a><input
							id="cancel${model.extraClass}${model.portletId}" type="reset"
							value='<spring:message code="integration.button.cancel"/>'
							class="cancel${model.extraClass}"></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
