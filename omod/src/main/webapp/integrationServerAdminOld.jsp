<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<openmrs:require privilege="View Server" otherwise="/login.htm" redirect="/module/dhisintegration/integrationServerAdmin.form" />
<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		
		$j('#addOrEditPopup').dialog({
			autoOpen: false,
			modal: true,
			title: 'Add Or Edit Server',
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
		
		$j(".newdetails").click( function() {
			$j('#addOrEditPopup').dialog('open');
		});
		
		$j(".cancel").click( function() {
			$j('#addOrEditPopup').dialog('close');		
		});

	} )};
	
	
	function confirmDelete(name) {
		if(confirm('<spring:message code="dhisintegration.confirm.serverDeletion"/>'))
		{
			 $j.post("${pageContext.request.contextPath}/module/dhisintegration/deleteServer.form",
			 		{serverName: name} );	
			 location.reload();
		}
	}
		
	function updateServer(name) {
		if(confirm('<spring:message code="dhisintegration.confirm.serverUpdate"/>'))
		{
			 $j.post("${pageContext.request.contextPath}/module/dhisintegration/updateServer.form?serverName"+name);	
			 location.reload();
		}
	}

	function editServer(id) {
	

			$j("#id").val($j.trim($j("#sid"+id).html()));
			$j("#servername").val($j.trim($j("#sname"+id).html()));
			$j("#description").val($j.trim($j("#sdescription"+id).html()));
			$j("#url").val($j.trim($j("#surl"+id).html()));
			$j("#uname").val($j.trim($j("#suserName"+id).html()));
			$j("#password").val($j.trim($j("#spassword"+id).html()));
			$j("#emailurl").val($j.trim($j("#semail"+id).html()));
			$j("#masterTemplate").val($j.trim($j("#smasterTemplate"+id).html()));
			$j("#transportType").val($j.trim($j("#stransportType"+id).html()));
			$j('#addOrEditPopup').dialog('open');
		}
}

	function changeVisibility(divID) {
		if (document.getElementById(divID)) {
			var item = document.getElementById(divID);
			item.style.display=(item.style.display=='block')?'none':'block';
		}
	} 	

	function updateServerData(name) {
					
				 $j.post("${pageContext.request.contextPath}/module/dhisintegration/getServerMetadata.form",
				 		{serverName: name}, 
				 		function() {
					 		alert('server data updated');
	            		}
	            ).done(function() {
	            };
	}
</script>

<style>
	.small { font-size: x-small; }
</style>

<h2><spring:message code="dhisintegration.serverAdmin"/></h2>

<div id="testConnectionPortlet" display="none">
			<openmrs:portlet url="dhisApi.portlet" id="testConnection_${serverItem.serverName}" 
					moduleId="dhisintegration" parameters="operation=TEST|server=${serverItem.serverName}" />

</div>
<div >
		       			<table id="table" class="dhisintegration-data-table display">
			<thead>
				<tr>
					<th><spring:message code="dhisintegration.general.name"/></th>
					<th><spring:message code="dhisintegration.general.description"/></th>
					<th><spring:message code="dhisintegration.general.url"/></th>
					<th><spring:message code="dhisintegration.general.lastUpdated"/></th>
					<th align="center" width="1%"><spring:message code="dhisintegration.general.actions"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serverItems}" var="serverItem" >
					<tr id="${serverItem.id}" >
					<td id="sid${serverItem.id}" STYLE=display:NONE >
							${serverItem.id}
						</td>
						<td width="20%" nowrap id="sname${serverItem.id}">
							${serverItem.serverName}
						</td>
						<td width="20%" id="sdescription${serverItem.id}">
							${serverItem.serverDescription}
						</td>
						<td width="20%" id="surl${serverItem.id}">
							${serverItem.url}
						</td>
						<td width="10%" id="lastUpdated${serverItem.id}">
							${serverItem.lastUpdated}
						</td>
						<td id="suserName${serverItem.id}" STYLE=display:NONE>
							${serverItem.userName}
						</td>
						<td id="spassword${serverItem.id}" STYLE=display:NONE>
							${serverItem.password}
						</td>
						<td id="semail${serverItem.id}" STYLE=display:NONE>
							${serverItem.emailorurl}
						</td>
						<td id="smasterTemplate${serverItem.id}" STYLE=display:NONE>
							${serverItem.masterTemplate}
						</td>
						<td id="stransportType${serverItem.transportType}" STYLE=display:NONE>
							${serverItem.transportType}
						</td>
						<td width="1%" align="center" nowrap >
						&nbsp;
						<openmrs:hasPrivilege privilege="Manage Locations">
							<a href="locationMapping.form?name=${serverItem.serverName}">
							</openmrs:hasPrivilege>
										<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/mapicon.png" border="0" title='<spring:message code="dhisintegration.tooltips.locationMapping"/>'/>	
									<openmrs:hasPrivilege privilege="Manage Locations">
									</a>
									</openmrs:hasPrivilege>
						&nbsp;
						<openmrs:hasPrivilege privilege="Manage Report Templates">
							<a href="manageReportTemplates.form?name=${serverItem.serverName}">
							</openmrs:hasPrivilege>
										<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/attributes.png" border="0" title='<spring:message code="dhisintegration.tooltips.viewReportTemplates"/>'/>	
									<openmrs:hasPrivilege privilege="Manage Report Templates">
									</a>
									</openmrs:hasPrivilege>
							&nbsp;
							<openmrs:hasPrivilege privilege="Manage Server">
							<a href="javascript:editServer('${serverItem.id}');">
							</openmrs:hasPrivilege>
							<img src="<c:url value='/images/edit.gif'/>" border="0" title='<spring:message code="dhisintegration.tooltips.editServer"/>'/>
							<openmrs:hasPrivilege privilege="Manage Server">
							</a>
							</openmrs:hasPrivilege>
							&nbsp;
							<openmrs:hasPrivilege privilege="Manage Server">
							<a href="javascript:confirmDelete('${serverItem.serverName}');">
							</openmrs:hasPrivilege>
							<img src="<c:url value='/images/trash.gif'/>" border="0" title='<spring:message code="dhisintegration.tooltips.deleteServer"/>'/>
							<openmrs:hasPrivilege privilege="Manage Server">
							</a>
							</openmrs:hasPrivilege>
							&nbsp;

							<a href="javascript:changeVisibility('testConnectionPortlet');">
								<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/lightning-icon.png" border="0" title='<spring:message code="dhisintegration.tooltips.testServerConnection"/>'/>	
							</a>
									
							&nbsp;
							<openmrs:hasPrivilege privilege="Manage Server">
							<a href="javascript:updateServerData'${serverItem.serverName}');">
							</openmrs:hasPrivilege>
							<a href="updateServer.form?name=${serverItem.serverName}">
										<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/Updateicon.png" border="0" title='<spring:message code="dhisintegration.tooltips.updateServerData"/>'/>	
									<openmrs:hasPrivilege privilege="Manage Server">
									</a>
									</openmrs:hasPrivilege>
									
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>
		<div id="addOrEditPopup">
					
					<div>
						<form:form modelAttribute="integrationServer" method="post" id="detailsedit" action="saveIntegrationServer.form" >
					<table>
						<tbody>	
							<tr>
								<td><spring:message code="dhisintegration.general.name"/></td>
								<td>:</td>
								<td>
								<form:hidden path="id" id="id"/>
								<form:input path="serverName" id="servername" size="40" /></td>
							</tr>
							<tr>
								<td><spring:message code="dhisintegration.general.description"/></td>
								<td>:</td>
								<td>
								<form:hidden path="masterTemplate" id="masterTemplate"/>
								<form:input path="serverDescription" id="description" size="40" /></td>
							</tr>
							<tr>
								<td><spring:message code="dhisintegration.general.url"/></td>
								<td>:</td>
								<td><form:input path="url" id="url" size="40" /></td>
							</tr>
							<tr>
								<td><spring:message code="dhisintegration.general.userName"/></td>
								<td>:</td>
								<td><form:input path="userName" id="uname" size="20" /></td>
							</tr>
							<tr>
								<td><spring:message code="dhisintegration.general.password"/></td>
								<td>:</td>
								<td><form:password path="password" id="password" size="20"/></td>
							</tr>
							<tr>
								<td><spring:message code="dhisintegration.general.transport"/></td>
								<td>:</td>
								<td>
									<form:select path="transportType" id="transportType">
									  <option value="Email"><spring:message code="dhisintegration.general.email"/></option>
									  <option value="Url"><spring:message code="dhisintegration.general.url"/></option>

									 </form:select>
									<form:input path="emailorurl" id="emailurl" size="20"/>
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
								<td><input type="submit" name="submit" value='<spring:message code="dhisintegration.button.save"/>'/> <input
								type="reset" value='<spring:message code="dhisintegration.button.cancel"/>' class="cancel">
								</td>
							</tr>
						</tbody>
					</table>
				</form:form>
					</div>				
				</div>
		 <div id="button" align="right">
                    <button id="addNew" class="newdetails">
                   <spring:message code="dhisintegration.button.addNew"/>
                    </button>
            </div>
		</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>