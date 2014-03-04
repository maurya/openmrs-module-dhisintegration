<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/dhisintegration/runReports.form" />
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		
		$('.reportParameters').dialog({
			autoOpen: false,
			modal: true,
			title: 'Parameters Mapping',
			width: '90%'
		});
		$(".report-table").dataTable( {
			"bPaginate": false,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );
		
		$(".cancel").click( function() {
			var id=this.id.replace("cancel","");
			$('#reportParameters'+id.trim()).dialog('close');	
			
		});
	} );
	
	function openReportMapping(id) {
			$('#reportParameters'+id).dialog('open');
		}
	
	function runReports(id) {
		var startDate=$("#StartDate"+id).val();
		var endDate=$("#EndDate"+id).val();
		var Location=$("#dhisLocations"+id).val();
		 $.post("${pageContext.request.contextPath}/module/dhisintegration/submitReportMappings.form",{startDate: startDate,endDate: endDate,Location: Location},function() {
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
<h2><spring:message code="dhisintegration.header.reportsToRun"/></h2>

<div >
		   <table class="report-table display">
			<thead>
				<tr>
					<th><spring:message code="dhisintegration.general.name"/></th>
					<th><spring:message code="dhisintegration.general.code"/></th>
					<th><spring:message code="dhisintegration.general.frequency"/></th>
					<th><spring:message code="dhisintegration.general.reportMappedTo"/></th>
					<th align="center" width="1%"><spring:message code="dhisintegration.general.actions"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reports}" var="report" >
					<tr id="${report.id}">
						<td width="10%" id="name${report.id}">
							${report.name}
						</td>
						<td width="10%" id="code${report.id}">
							${report.code}
						</td>
						<td width="10%" id="frequency${report.id}">
							${report.frequency}
						</td>
						<td width="10%" id="mappedReport${report.id}">
						 ${uuidToReportDefinitionMap[report.mappedReportUuid]}
						</td>
						<td align="center" nowrap>
							&nbsp;
							<a href="javascript:openReportMapping('${report.id}');"><img src="<c:url value='/images/play.gif'/>" border="0" title='<spring:message code="dhisintegration.runReports"/>'/></a>
							<div id="reportParameters${report.id}" class="reportParameters">
						<openmrs:portlet url="reportParamsMapping.portlet" id="reportParamsMapping${report.id}" moduleId="dhisintegration" parameters="portletId=${report.id}" />
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