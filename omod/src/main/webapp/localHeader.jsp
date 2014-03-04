<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/integrationServerAdmin") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/integration/integrationServerAdmin.form"><spring:message
				code="integration.serverAdmin" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/locationMapping") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/integration/locationMapping.form"><spring:message
				code="integration.tooltips.locationMapping" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/runReports") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/integration/runReports.form"><spring:message
				code="integration.runReports" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
