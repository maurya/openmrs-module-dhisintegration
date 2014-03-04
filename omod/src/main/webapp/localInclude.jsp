<!-- Include taglibs from core -->
<%@ taglib prefix="fn" uri="/WEB-INF/taglibs/fn.tld" %>
<%@ taglib prefix="springform" uri="http://www.springframework.org/tags/form" %>

<!-- Include css from core
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css"/>


<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js"/>


<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js"/>
 -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/dhisintegration/scripts/dataTables/css/page.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/dhisintegration/scripts/dataTables/css/table.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/dhisintegration/scripts/dataTables/css/custom.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/dhisintegration/scripts/css/dhistabletree.css"/>


<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>


<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/dhisintegration/scripts/dataTables/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/dhisintegration/scripts/jquery.jstree.js"/>

<script type="text/javascript">
	var $ = jQuery.noConflict();
</script>