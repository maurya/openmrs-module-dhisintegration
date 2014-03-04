<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<openmrs:require privilege="Manage Report Templates" otherwise="/login.htm" redirect="/module/integration/manageReportTemplates.form" />
<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
			$j('#report-table').dataTable( {
				"bPaginate": false,
				"iDisplayLength": 8,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bInfo": true,
				"bAutoWidth": false,
				"bScrollY" : true,
			} );
			
			$j('.radio').click(function() {
				var reportId=$j(this).attr('id').substring(5); // strip 'radio'
				alert(reportId);
			} );

			$j('.edit').click(function() {
				var reportId = $j(this).attr('id').substring(4); // strip 'edit'
				alert(reportId);
				$j("#id").val(reportId);
				$j("#reportName").val($j.trim($j("#name"+reportId).html()));
				$j("#mappedReport").val($j.trim($j("#mappedReport"+reportId).html()));
				$j("#baseCohort").val($j.trim($j("#baseCohort"+reportId).html()));
				$j('#editReportTemplateMapping').dialog('open');
			} );

			$j('.mapDE').click(function() {
				var reportId = $j(this).attr('id').substring(5); // strip 'mapDE'
				alert(reportId);
				var url = "showDataElements.form?reportTemplateId=" + reportId + "&server=${server.serverName}";
				alert(url);
				window.location.replace(url);
				} );

			$j('.mapOS').click(function() {
				var reportId = $j(this).attr('id').substring(5); // strip 'mapOS'
				alert(reportId);
				var url = "showOptions.form?reportTemplateId=" +reportId +"&server=${server.serverName}";
				alert(url);
				window.location.replace(url);
			} );
			
			$j('#editReportTemplateMapping').dialog({
				autoOpen: false,
				modal: true,
				title: '<openmrs:message code="integration.general.reportMappedTo" javaScriptEscape="true" />',
				width: '90%'
			} );
					
			$j(".cancel").click( function() {
				$j('#editReportTemplateMapping').dialog('close');	
			} );

			$j('#combo-table').dataTable( {
				"bPaginate": false,
				"iDisplayLength": 8,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bInfo": true,
				"bAutoWidth": false,
				"bScrollY" : true,
			} );
			
			$j('.deText').click(function() {
				var deId = $j(this).attr('id');
				alert("DE: " + deId);
			} );
			
			$j('.osText').click(function() {
				var osId = $j(this).attr('id');
				alert("OS: " + osId);
			} );
			
			var tReport = $j('#report-table');
			var tCombo = $j('#combo-table');
			var rid = "XXX";
			tCombo.fnFilter(rid,0,false,false,false,true);

			$j('.radio').click(function() {
				rid = $j(this).attr('id').substring(5);  //string radio
				alert("report: " + rid);
				tCombo.fnFilter(rid,0,false,false,false,true);
			} );

			
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
	<a href="integrationServerAdmin.form">
		<openmrs:message code="integration.return.serverAdministration"/>
	</a>|
</div>
<h2>
	<openmrs:message code="integration.general.reportTemplatesFor"/> 
	&nbsp;:&nbsp;${server.serverName}
</h2>

<div id="base-page"><form>
<div id="radio">
	<br/>
	<table id="report-table" width="90%">
		<thead>
			<tr>
				<th>&nbsp;</th>
				<th><openmrs:message code="integration.general.name"/></th>
				<th><openmrs:message code="integration.general.code"/></th>
				<th><openmrs:message code="integration.general.frequency"/></th>
				<th><openmrs:message code="integration.general.baseCohort"/></th>
				<th><openmrs:message code="integration.general.reportMappedTo"/></th>
				<th align="center" width="1%"><openmrs:message code="integration.general.actions"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${reportTemplates}" var="reportTemplate" varStatus="rtstatus">
				<tr id="${reportTemplate.id}" style="${rtstatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" >
					<td>
						<input id="radio${reportTemplate.id }" class="radio" type="radio"/>
					</td>
					<td width="20%" id="name${reportTemplate.id}" class="reportRow">
						${reportTemplate.name}
					</td>
					<td width="10%" id="code${reportTemplate.id}">
						${reportTemplate.code}
					</td>
					<td width="10%" id="frequency${reportTemplate.id}">
						${reportTemplate.frequency}
					</td>
						<td width="10%" id="baseCohort${reportTemplate.id}">
						${uuidToReportDefinitionMap[reportTemplate.mappedReportUuid].baseCohortDefinition}
					</td>
					<td width="10%" id="mappedReport${reportTemplate.id}">
						${uuidToReportDefinitionMap[reportTemplate.mappedReportUuid].name}
					</td>
					<td align="center" nowrap>
						<button type="button" class="edit" id="edit${reportTemplate.id}">
                 			<openmrs:message code="integration.button.editReports"/>
                   		</button>

						<button type="button" class="mapDE" id="mapDE${reportTemplate.id}">
                  			<openmrs:message code="integration.button.mapDataElement"/>
                   		</button>

                     	<button type="button" class="mapOS" id="mapOS${reportTemplate.id}">
                  			<openmrs:message code="integration.button.mapOptionSets"/>
                   		</button>
                    </td>
				</tr>
			</c:forEach>	
		</tbody>
	</table>
</div>
	
<div id="report-mapping">
	<br/>
	<table id="combo-table" width="90%">
		<thead>
			<tr>
				<th style="display:none" />
				<th><openmrs:message code="integration.dhis.dataElement"/></th>
				<th><openmrs:message code="integration.dhis.categoryCombo"/></th>
				<th><openmrs:message code="integration.dhis.optionSet"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${reportMapDisplay}" var="rd" varStatus="rdstatus">
				<tr id="rdrow${rd.reportId}" class='${status.index % 2 == 0 ? "evenRow" : "oddRow"}' >
					<td id="rpt${rd.reportId }" style="display:none">
						${rd.reportId }
					</td>
					<td>
						<c:forEach items="${rd.elements}" var="de">
							<button type="button" id="${de.id }" class="deText"  >
								<img src="<c:url value='/images/edit.gif'/>" border="0" />
							</button>
							${de.name}
							<c:if test="${de.mapped}">
								<img src="<c:url value='/images/checkmark.png'/>" border="0" />
							</c:if>
							<br/>
						</c:forEach>
					</td>
					<td id="${rd.comboId }">
						${rd.comboName }
					</td>
					<td style="background-color:${rdstatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" >
						<c:forEach items="${rd.optionSets}" var="os">
							<button type="button" id="${os.id}" class="osText" >
								<img src="<c:url value='/images/edit.gif'/>" border="0" />
							</button>
							${os.name}
							<c:if test="${os.mapped}">
								<img src="<c:url value='/images/checkmark.png'/>" border="0" />
							</c:if>
							<br/>
						</c:forEach>
					</td>
			</c:forEach>
		</tbody>
	</table>
</div>
</form></div>

<div id="editReportTemplateMapping">				
	<form method="post" id="detailsedit" action="saveReportTemplateMapping.form">
		<table>
			<tbody>	
				<tr>
					<td><openmrs:message code="integration.general.name"/></td>
					<td>:</td>
					<td>
						<input id="id" type="hidden"/>
						<input id="reportName" type="text" width="50" input="disabled" />
					</td>
				</tr>
				<tr>
					<td><openmrs:message code="integration.general.reportMappedTo"/></td>
					<td>:</td>
					<td> <input id="mappedReport" type="text" size="40" /></td>
				</tr>
				<tr>
					<td><openmrs:message code="integration.general.baseCohort"/></td>
					<td>:</td>
					<td><input id="baseCohort" type="text" size="40" /></td>
				</tr>
				<tr></tr>
				<tr>
					<td></td>
					<td></td>
					<td>
						<input type="submit" name="submit" value='<openmrs:message code="integration.button.save"/>' javaScriptEscape="true" /> 
						<input type="reset" id="cancel" value='<openmrs:message code="integration.button.cancel" javaScriptEscape="true"/>' class="cancel">
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>