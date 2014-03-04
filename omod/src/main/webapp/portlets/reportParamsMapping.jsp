<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />	
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
	});
		</script>
		<div>
					<div >
					<form method="post" id="detailsedit" >
					<table>
						<tbody>	
							<tr>
								<td>Start Date</td>
								<td>:</td>
								<td>
								<input id="StartDate${model.portletId}" name="StartDate" type="text" size="10" onClick="showCalendar(this);"/></td>
						</tr>
							<tr>
								<td>End Date</td>
								<td>:</td>
								<td>
								<input id="EndDate${model.portletId}" name="EndDate" type="text" size="10" onClick="showCalendar(this);"/></td>
						</tr>
							<tr>
								<td>Location</td>
								<td>:</td>
								<td>
   							<select name='dhisLocations${model.portletId}' id='dhisLocations${model.portletId}'>
								<option value=""></option>
   								 		<c:forEach items="${model.dhisOrgUnits}" var="orgUnit">
										       <option id="${orgUnit.id}" value="${orgUnit.id}">${orgUnit}</option>
   							 			</c:forEach>
								</select>
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
								<td><a href="javascript:runReport(${model.portletId});"><input id="submit${model.portletId}" type="button" value='<spring:message code="dhisintegration.button.submit"/>' /> </a><input id="cancel${model.portletId}"
								type="reset" value='<spring:message code="dhisintegration.button.cancel"/>' class="cancel">
								</td>
							</tr>
						</tbody>
					</table>
				</form>
					</div>					
		</div>