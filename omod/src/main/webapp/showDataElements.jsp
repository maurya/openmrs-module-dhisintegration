<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<openmrs:require privilege="Manage Locations" otherwise="/login.htm" redirect="/module/dhisintegration/showDataElements.form" />
<script type="text/javascript" charset="utf-8">
	var win;	// child cohort def window
	
	$j(document).ready(function() {
		
		$j('.addOrEditPopup').dialog({
			autoOpen: false,
			modal: true,
			title: 'Map Data Element',
			width: '90%'
		});
		
		$j(".dhisintegration-data-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 5,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

		$j(".btnSubmit").click( function() {
			var id = $(this).attr("id").substring(6);
			alert (id);
		});
		
		$j(".btnCancel").click( function() {
			var id = $(this).attr("id").substring(6);
			alert (id);
		});
		
		$j(".btnCreate").click( function() {
			var id = $(this).attr("id").substring(6);
			alert (id);
			if (win) {
				win.open(,"CohortDefWindow");
			} else {
				win = window.open("/openmrs/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition","CohortDefWindow");
			}
			win.focus();
		})
	} );

</script>

<style>
	.small { font-size: x-small; }
	.oddRow { background-color: white;}
	.evenRow { background-color: gainsboro; }
	.deText { border-style: none;}
	.osText { border-style: none;}
</style>

<div id="breadCrumbs">
	<a href="integrationServerAdmin.form"><spring:message code="dhisintegration.return.serverAdministration"/></a>|<a href="manageReportTemplates.form?name=${server}"><spring:message code="dhisintegration.return.reportTemplates"/></a>|
</div>
<h2>
	<spring:message code="dhisintegration.dhis.reportTemplate"/>
	&nbsp;:&nbsp;${reportTemplate.name}
</h2>
<div >
	<table class="dhisintegration-data-table">
		<thead>
			<tr>
				<th><spring:message code="dhisintegration.dhis.dataElement"/></th>
				<th><spring:message code="dhisintegration.dhis.categoryCombo"/></th>
				<th><spring:message code="dhisintegration.general.mappedTo"/></th>
				<th align="center" width="1%" colspan="2">
					<spring:message code="dhisintegration.general.editMapping"/>
				</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${DataElementToCategoryComboDictionary}" var="element"  varStatus="elid">
				<tr id="${element.key.id}"  style="${elid.index % 2 == 0 ? 'evenRow' : 'oddRow'}" >
					<td width="10%" id="name${element.key.id}" >
						${element.key.name}
					</td>
					<td width="20%">
						${element.value.name}
					</td>
						<td width="1%" align="center" nowrap>
							&nbsp;

							<a href="javascript:editDataElement('${element.key.id}');">
								<img src="<c:url value='/images/edit.gif'/>" border="0" title='<spring:message code="dhisintegration.tooltips.mapDataElement"/>'/>
							</a>
							<div id="addOrEditPopup${element.key.id}" class="addOrEditPopup">
								<openmrs:portlet url="mappingCohort.portlet" id="mappingCohort${element.key.id}" moduleId="dhisintegration" parameters="type=DataElement|mappedCohort=${element.key.cohortDefinitionUuid}|portletId=${element.key.id}" />
							</div>
						</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
		</tfoot>
	</table>
</div>
		
<%@ include file="/WEB-INF/template/footer.jsp"%>