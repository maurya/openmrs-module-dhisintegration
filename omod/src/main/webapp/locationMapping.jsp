<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<openmrs:require privilege="Manage Locations" otherwise="/login.htm" redirect="/module/dhisintegration/locationMapping.form" />
 <script>
            $(document).ready(function(){
              
                    
           $(".dhisintegration-data-table").dataTable( {
			"bPaginate": false,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": false,
			"bAutoWidth": false
		} );
           
           $("#treeViewDiv1").jstree({
            	  "json_data" : {
                       "data" : ${json}			  
					  },"themes" : {
			"theme" : "apple",
			"dots" : false,
			"icons" : false
		},
                    "plugins" : [ "themes", "json_data", "ui" ]
                });
                    
              $("#treeViewDiv2").jstree({
            	  "json_data" : {
                      "data":[
                          {
                              "data" : "Search engines",
                              "children" :[
                                           {"data":"Yahoo"},
                                           {"data":"Bing"},
                                           {"data":"Google", "children":[{"data":"Youtube"},{"data":"Gmail"},{"data":"Orkut"}]}
                                          ]
                          },
                          {
                              "data" : "Networking sites",
                              "children" :[
                                  {"data":"Facebook"},
                                  {"data":"Twitter"}
                              ]
                          }
                      ]
                  },"themes" : {
			"theme" : "apple",
			"dots" : false,
			"icons" : false
		},
                    "plugins" : [ "themes", "json_data", "ui" ]
                });
				});
                
			function sendorgunit() {
			var node=$("#treeViewDiv1").jstree("get_selected").text();
			alert(node);
		}

			function updateOrgs(name) {
				<div id="${name}" class="addOrEditPopup">
					<openmrs:portlet url="dhisApi.portlet" id="orgUpdate_${name}" 
							moduleId="dhisintegration" parameters="operation=ORGS_API|server=${name}" />
				</div>
			}
				

			</script>
    <div id="breadCrumbs">
<a href="integrationServerAdmin.form"><spring:message code="dhisintegration.return.serverAdministration"/></a>|
</div>
<h2><spring:message code="dhisintegration.general.locationsFor"/> <spring:message code="dhisintegration.serverAdmin"/> : ${server.serverName}</h2>
	<a align="right" href="javascript:updateOrgs'${serverItem.serverName}');">
		<spring:message code="dhisintegration.general.updateDhisLocations"/>
	</a>

	    <div id=mainDiv>
	        <div id="treeViewDiv1">
	        </div>
			<a href="javascript:sendorgunit();">
			<button class="dhisbutton" >
                   Send into Org Units
                    </button>
					</a>
	        <div id="selectionDiv">
	        	<table id="table" class="dhisintegration-data-table display">
			<thead>
				<tr>
					<th><spring:message code="dhisintegration.general.dhisOrgUnits"/></th>
					<th><spring:message code="dhisintegration.general.openMRSLocations"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serverItems}" var="serverItem" >
					<tr id="${serverItem.id}" >
					<td id="sid${serverItem.id}" >
							${serverItem.id}
						</td>
						<td width="20%" nowrap id="sname${serverItem.id}">
							${serverItem.serverName}
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>
	        </div>
			<button class="dhisbutton" >
				Add for the Org unit
                    </button>
	        <div id="treeViewDiv2">
	        </div>
	   </div>