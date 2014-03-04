<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<openmrs:require privilege="View Server" otherwise="/login.htm" redirect="/module/dhisintegration/integrationServerAdmin.form" />

	<script type="text/javascript" charset="utf-8">
		$j(document).ready(function() {
		
			$j(".dhisintegration-data-table").dataTable( {
				"bPaginate": false,
				"iDisplayLength": 25,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": true,
				"bInfo": true,
				"bAutoWidth": false,
				"closeOnEscape": true
			} );
			
			$j('.delete').click(function() {
				var serverName = $j(this).attr('id').substring(6); // strip 'delete'
				alert(serverName);
				if(confirm('<openmrs:message code="dhisintegration.confirm.serverDeletion" javaScriptEscape="true" />'))
				{
//					 $j.post("${pageContext.request.contextPath}/module/dhisintegration/deleteServer.form",
//					 		{serverName: name} );	
//					 location.reload();
				}
			} );
		
			$j('.update').click(function() {
				var serverName = $j(this).attr('id').substring(6); // strip 'update'
				alert(serverName);
				var url = window.location.href;
				url = url.substr(0,url.lastIndexOf("/")+1);
				url = url + "deleteServer.form?name=" + serverName;
				if(confirm('<openmrs:message code="dhisintegration.confirm.serverUpdate" javaScriptEscape="true" />'))
				{
					window.location.href=url;
				}
			} );
		
			$j('.test').click(function() {
				var serverName = $j(this).attr('id').substring(4); // strip 'test'
				alert(serverName);
//					 $j.post("${pageContext.request.contextPath}/module/dhisintegration/deleteServer.form",
//					 		{serverName: name} );	
//					 location.reload();
			} );
		
			$j('.templates').click(function() {
				var serverName = $j(this).attr('id').substring(9); // strip 'templates'
				var url = window.location.href;
				url = url.substr(0,url.lastIndexOf("/")+1);
				url = url + "manageReportTemplates.form?name=" + serverName;
				window.location.href=url;
			} );
		
			$j('.locations').click(function() {
				var serverName = $j(this).attr('id').substring(9); // strip 'locations'
				var url = window.location.href;
				url = url.substr(0,url.lastIndexOf("/")+1);
				url = url + "locationMapping.form?name=" + serverName;
				window.location.href=url;
			} );
		
			$j('.edit').click(function() {
				var serverName = $j(this).attr('id').substring(4); // strip 'edit'
				$j("#id").val($j.trim($j("#sid"+serverName).html()));
				$j("#servername").val($j.trim($j("#sname"+serverName).html()));
				$j("#description").val($j.trim($j("#sdescription"+serverName).html()));
				$j("#url").val($j.trim($j("#surl"+serverName).html()));
				$j("#uname").val($j.trim($j("#suserName"+serverName).html()));
				$j("#password").val($j.trim($j("#spassword"+serverName).html()));
				$j("#emailurl").val($j.trim($j("#semail"+serverName).html()));
				$j("#masterTemplate").val($j.trim($j("#smasterTemplate"+serverName).html()));
				$j("#transportType").val($j.trim($j("#stransportType"+serverName).html()));
				$j('#addOrEditPopup').dialog('open');
			} );

			$j('#addOrEditPopup').dialog({
				autoOpen: false,
				modal: true,
				title: '<openmrs:message code="dhisintegration.general.addOrUpdate" javaScriptEscape="true" />',
				width: '90%'
			});
					
			$j("#addNewServer").click( function() {
				$j('#addOrEditPopup').dialog('open');
			});
			
			$j("cancelNewServer").click( function() {
				$j('#addOrEditPopup').dialog('close');		
			});

		} );
		
	</script>

	<style>
		.small { font-size: x-small; }
		.oddRow { background-color: white; }
		.evenRow { background-color: gainsboro; }
	</style>
	
	
	<h2><openmrs:message code="dhisintegration.serverAdmin" javaScriptEscape="true"/></h2>

	<div>
		<table id="table" class="dhisintegration-data-table display">
			<thead>
				<tr>
					<th><openmrs:message code="dhisintegration.general.name" javaScriptEscape="true" /></th>
					<th><openmrs:message code="dhisintegration.general.description" javaScriptEscape="true" /></th>
					<th><openmrs:message code="dhisintegration.general.url" javaScriptEscape="true" /></th>
					<th><openmrs:message code="dhisintegration.general.lastUpdated" javaScriptEscape="true" /></th>
					<th align="center" width="1%"><openmrs:message code="dhisintegration.general.actions" javaScriptEscape="true" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serverItems}" var="serverItem" varStatus="status" >
					<tr class='${status.index % 2 == 0 ? "oddRow" : "evenRow" }' id="${serverItem.id}" >
						<td id="sid${serverItem.serverName}" STYLE=display:NONE >
							${serverItem.id}
						</td>
						<td width="20%" nowrap id="sname${serverItem.serverName}">
							${serverItem.serverName}
						</td>
						<td width="20%" id="sdescription${serverItem.serverName}">
							${serverItem.serverDescription}
						</td>
						<td width="20%" id="surl${serverItem.serverName}">
							${serverItem.url}
						</td>
						<td width="10%" id="lastUpdated${serverItem.serverName}">
							${serverItem.lastUpdated}
						</td>
						<td id="suserName${serverItem.serverName}" STYLE=display:NONE>
							${serverItem.userName}
						</td>
						<td id="spassword${serverItem.serverName}" STYLE=display:NONE>
							${serverItem.password}
						</td>
						<td id="semail${serverItem.serverName}" STYLE=display:NONE>
							${serverItem.emailorurl}
						</td>
						<td id="smasterTemplate${serverItem.serverName}" STYLE=display:NONE>
							${serverItem.masterTemplate}
						</td>
						<td id="stransportType${serverItem.serverName}" STYLE=display:NONE>
							${serverItem.transportType}
						</td>
						<td width="1%" align="center" nowrap >
							&nbsp;

							<openmrs:hasPrivilege privilege="Manage Server">
								<img id="locations${serverItem.serverName }" class="locations" width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/mapicon.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.locationMapping" javaScriptEscape="true" />'/>	
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Manage Server" inverse="true">
								<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/mapicon.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.locationMapping" javaScriptEscape="true" />'/>	
							</openmrs:hasPrivilege>
							&nbsp;

							<openmrs:hasPrivilege privilege="Manage Report Templates">
								<img id="templates${serverItem.serverName }" class="templates" width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/integration/images/attributes.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.viewReportTemplates" javaScriptEscape="true" />'/>	
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Manage Report Templates" inverse="true">
								<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/attributes.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.viewReportTemplates" javaScriptEscape="true"/>'/>	
							</openmrs:hasPrivilege>
							&nbsp;

							<openmrs:hasPrivilege privilege="Manage Server">
								<img id="edit${serverItem.serverName }" class="edit" src="<c:url value='/images/edit.gif'/>" border="0" title='<openmrs:message code="dhisintegration.tooltips.editServer" javaScriptEscape="true" />'/>
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Manage Server" inverse="true">
								<img src="<c:url value='/images/edit.gif'/>" border="0" title='<openmrs:message code="dhisintegration.tooltips.editServer" javaScriptEscape="true" />'/>
							</openmrs:hasPrivilege>
							&nbsp;
							
							<openmrs:hasPrivilege privilege="Manage Server">
								<img id="delete${serverItem.serverName }" class="delete" src="<c:url value='/images/trash.gif'/>" border="0" title='<openmrs:message code="dhisintegration.tooltips.deleteServer" javaScriptEscape="true" />'/>
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Manage Server" inverse="true">
								<img src="<c:url value='/images/trash.gif'/>" border="0" title='<openmrs:message code="dhisintegration.tooltips.deleteServer" javaScriptEscape="true" />'/>
							</openmrs:hasPrivilege>
							&nbsp;
		
							<img id="test${serverItem.serverName }" class="test" width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/lightning-icon.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.testServerConnection" javaScriptEscape="true" />'/>	
									
							&nbsp;
							<openmrs:hasPrivilege privilege="Manage Server">
								<img id="update${serverItem.serverName }" class="update" width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/Updateicon.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.updateServerData" javaScriptEscape="true"/>'/>	
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Manage Server" inverse="true">
								<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/dhisintegration/images/Updateicon.png" border="0" title='<openmrs:message code="dhisintegration.tooltips.updateServerData" javaScriptEscape="true" />'/>	
							</openmrs:hasPrivilege>
									
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>

		<div id="button" align="right">
			<button id="addNewServer" type="button" class="newdetails">
	        	<openmrs:message code="dhisintegration.button.addNew" javaScriptEscape="true" />
	        </button>
	    </div>
	</div>

	<div id="addOrEditPopup">
		<form modelAttribute="integrationServer" method="post" id="detailsedit" action="saveIntegrationServer.form" >
			<table>
				<tbody>	
					<tr>
						<td><openmrs:message code="dhisintegration.general.name" javaScriptEscape="true" /></td>
						<td>:</td>
						<td>
							<input type="hidden" name="id" id="id"/>
							<input type="text" name="serverName" id="servername" size="40" />
						</td>
					</tr>
					<tr>
						<td><openmrs:message code="dhisintegration.general.description" javaScriptEscape="true" /></td>
						<td>:</td>
						<td>
							<input type="hidden" name="masterTemplate" id="masterTemplate"/>
							<input type="text" name="serverDescription" id="description" size="40" />
						</td>
					</tr>
					<tr>
						<td><openmrs:message code="dhisintegration.general.url" javaScriptEscape="true" /></td>
						<td>:</td>
						<td><input type="text" name="url" id="url" size="40" /></td>
					</tr>
					<tr>
						<td><openmrs:message code="dhisintegration.general.userName" javaScriptEscape="true" /></td>
						<td>:</td>
						<td><input type="text" name="userName" id="uname" size="20" /></td>
					</tr>
					<tr>
						<td><openmrs:message code="dhisintegration.general.password" javaScriptEscape="true" /></td>
						<td>:</td>
						<td><input type="text" name="password" id="password" size="20"/></td>
					</tr>
					<tr>
						<td><openmrs:message code="dhisintegration.general.transport" javaScriptEscape="true" /></td>
						<td>:</td>
						<td>
							<select name="transportType" id="transportType">
								<option value="Email"><openmrs:message code="dhisintegration.general.email" javaScriptEscape="true" /></option>
							  	<option value="Url"><openmrs:message code="dhisintegration.general.url" javaScriptEscape="true" /></option>
							</select>
							<input type="text" name="emailorurl" id="emailurl" size="20"/>
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
						<td>
							<input type="submit" name="submit" value='<openmrs:message code="dhisintegration.button.save"/>' javaScriptEscape="true" /> 
							<input type="reset" id="cancelNewServer" value='<openmrs:message code="dhisintegration.button.cancel" javaScriptEscape="true"/>' class="cancel">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	
	<div id="dhisApiPopup">
		<form>
			<h3>
				<c:if test="${attributes.apiresult.error}">
					<img width="20" height="20" src="/images/error.gif" border="0"'/>	
				</c:if>
				${resultStatus }
			</h3>
		</form>
	</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>