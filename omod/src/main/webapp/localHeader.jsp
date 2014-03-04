<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/integrationServerAdmin") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/dhisintegration/integrationServerAdmin.form"><spring:message
				code="dhisintegration.serverAdmin" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/locationMapping") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/dhisintegration/locationMapping.form"><spring:message
				code="dhisintegration.tooltips.locationMapping" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/runReports") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/dhisintegration/runReports.form"><spring:message
				code="dhisintegration.runReports" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
