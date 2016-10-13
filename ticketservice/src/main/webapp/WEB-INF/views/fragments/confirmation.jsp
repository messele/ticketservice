<%@ include file="include.jsp"%>
<spring:url value="/" var="holdSeatsUrl"></spring:url>
<div class="jumbotron">
	<c:if test="${not empty error}">
		<div class="alert alert-danger">${error}</div>
	
	</c:if>
	
	<c:if test="${not empty confirmationCode}">
		<div class="alert alert-success"> Your Reservation has Succeeded. Confirmation Code : ${confirmationCode}</div>
	</c:if>
	
		<a class="btn" href="${baseUri}"> Return to home page</a>
</div>

