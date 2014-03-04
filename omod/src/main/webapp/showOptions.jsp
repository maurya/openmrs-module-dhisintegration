<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp"%>
<openmrs:require privilege="Manage Options" otherwise="/login.htm"
	redirect="/module/dhisintegration/showOptions.form" />
<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		
		$j('.addOrEditPopup').dialog({
			autoOpen: false,
			modal: true,
			title: 'Map Option',
			width: '90%'
		});
		
		$j(".dhisintegration-data-table").dataTable( {
			"bPaginate": false,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );
		
		$j(".cancel").click( function() {
			
			var id=this.id.replace("cancel","");
			$('#addOrEditPopup'+id).dialog('close');	
			
		});

	} );

	function editOption(id) {
	
			$("#id"+id).val(id);

			$("#OptionName"+id).val($j.trim($("#name"+id).html()));
			$('#addOrEditPopup'+id).dialog('open');
		}
		function saveOption(id) {
	
			var uuid=$("#cohorts"+id).val();
			 $j.post("${pageContext.request.contextPath}/module/dhisintegration/saveOptionsSetMapping.form",{uuid: uuid,id: id},function() {
	               //alert('got data');
	          }).error(function() {
		               // alert('Unable load Templates');
		            }).success(function() {
		                // alert('success');
		            }).complete(function() {
		               //alert('complete');
					   location.reload();
		            });
		}
</script>
<div id="breadCrumbs">
	<a href="integrationServerAdmin.form"><spring:message
			code="dhisintegration.return.serverAdministration" /></a>|<a
		href="manageReportTemplates.form?name=${server}"><spring:message
			code="dhisintegration.return.reportTemplates" /></a>|
</div>
<h2>
	<spring:message code="dhisintegration.header.optionSetsReport" />
	: ${reportTemplate.name}
</h2>

<div>
	<table class="dhisintegration-data-table display">
		<thead>
			<tr>
				<th><spring:message code="dhisintegration.dhis.optionSet" /></th>
				<th><spring:message code="dhisintegration.dhis.options" /></th>
				<th><spring:message code="dhisintegration.general.mappedTo" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${OptionSetList}" var="optionset">
				<tr>
					<td width="10%">${optionset.name}</td>

					<td width="20%"><c:forEach items="${optionset.options}"
							var="option">
							<p>
								<label id="name${option.id}">${option.name}</label>
								<a href="javascript:editOption('${option.id}');">
									<img src="<c:url value='/images/edit.gif'/>" border="0"
										title='<spring:message 
										code="dhisintegration.tooltips.mapOption"/>' />
								</a>
							</p>
						</c:forEach></td>
					<td><c:forEach items="${optionset.options}" var="option">
							<p>
								<label id="mapped${option.id}">${uuidToCohortDefinitionMap[option.cohortdefUuid]}</label>
								<br/>
							</p>
							<div id="addOrEditPopup${option.id}" class="addOrEditPopup">
								<openmrs:portlet url="mappingCohort.portlet"
									id="mappingCohort${option.id}" moduleId="dhisintegration"
									parameters="mappedCohort=${option.cohortdefUuid}|type=Option|portletId=${option.id}" />
							</div>
						</c:forEach></td>
				</tr>

			</c:forEach>
		</tbody>
		<tfoot>
		</tfoot>
	</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>