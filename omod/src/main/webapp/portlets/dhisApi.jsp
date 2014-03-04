<%@ include file="/WEB-INF/template/include.jsp"%>

<script type="text/javascript"	src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		var timer = setInterval(ajaxfunction(),3000);
	});
	
	function ajaxfunction() {
		$j.ajax({
			url: 'dhisApi.portlet?operation=STATUS',
			success: function() {
				if ((${model.done}=='DONE') || (${model.done}=='CANCEL')) {
					clearInterval(timer);
				}
			}
		});
	}
</script>

	<div>
		<div >
			<form method="get" id="apiresultdisplay" >
				<h2>
					<c:if test="empty ${model.done}">
						<spring:message code="dhisintegration.ApiPortlet.PleaseWait" />
					</c:if>
					<c:if test="!empty ${model.done}">
						<c:if test="${model.done!='DONE'}">
							<spring:message code="dhisintegration.ApiPortlet.PleaseWait" />
						</c:if>
						<c:if test="${model.done=='DONE'}">
							<c:if test="${attributes.apiresult.error}">
								<img width="20" height="20" src="/images/error.gif" border="0"'/>	
							</c:if>
							<spring:message code="${attributes.apiresult.status}" />
						</c:if>
					</c:if>
				</h2>
				
				<c:if test="!empty ${attributes.apiresult.changes}">
					<div id="updateresult">
						<table>
							<tbody>
								<tr>
									<th><spring:message code="dhisintegration.ApiPortlet.ChangeType" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.Object" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.UID" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.Name" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.Code" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.OldName" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.OldCode" /></th>
									<th><spring:message code="dhisintegration.ApiPortlet.Revision" /></th>
								</tr>
								<c:forEach items="${attributes.apiresult.changes}" var="change">
										<tr>
	 									<td>${change.changeType}</td>
	 									<td>${change.objClass}</td>
	 									<td>${change.uid}</td>
	 									<td>${change.name}</td>
	 									<td>${change.code}</td>
	 									<td>${change.oldName}</td>
	 									<td>${change.oldCode}</td>
	 									<td>${change.newFreq}</td>
	 								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:if>
				
				<c:if test="!empty ${attributes.apiresult.removed}">
					<div id="orgunitresult">
						<table>
							<tbody>
								<tr>
									<th><spring:message code="dhisintegration.ApiPortlet.DeletedOrgUnits" /></th>
								</tr>
								<c:forEach items="${attributes.apiresult.removed}" var="orgunit">
									<tr><td>orgunit.name</td></tr>
								</c:forEach>
							</tbody>
						</table>
					</div>	
				</c:if>
				
				<c:if test="!empty ${attributes.apiresult.summary}">
					<div id="summary">
						<table>
							<thead>
								<spring:message code="dhisintegration.ApiPortlet.SummaryStatus" />
								&nbsp;:&nbsp
								${attributes.apiresult.summary.status}
								&nbsp;:&nbsp
								${attributes.apiresult.summary.description}
							</thead>
							<tbody>
								<tr>
									<td><spring:message code="dhisintegration.ApiPortlet.DataValueCount" /></td>
									<td>${attributes.apiresult.summary.dataValueCount}</td>
								</tr>
								<tr>
									<td><spring:message code="dhisintegration.ApiPortlet.Conflicts" /></td>
									<td>${attributes.apiresult.summary.conflicts}</td>
								</tr>
							</tbody>
						</table>
					</div>
				</c:if>
				
				<c:if test="${model.done!='DONE'}">
					<input 
						id="cancel"
						type="reset" 
						value='<spring:message code="dhisintegration.button.cancel"/>' 
					/>
				</c:if>
				<c:if test="${model.done=='DONE'}">
					<input 
						id="ok" 
						type="submit" 
						value='<spring:message code="dhisintegration.button.OK"/>' 
					/>
				</c:if>
			</form>
		</div>					
	</div>
	