<%@ include file="include.jsp"%>
<spring:url value="/reserve" var="reserveSeatsUrl"></spring:url>
<div class="jumbotron">
<form:form modelAttribute="reserveSeatsInput" class="form-horizontal"
		id="hold-seats-form" action="${reserveSeatsUrl}"
		method="post">
		<form:hidden path="seatHoldId" />
		<form:hidden path="email" />
		<fieldset>
			<div class="form-group">
				<p> The following Seat (s) have been put on hold</p> <br/>
				${seats}
			</div>
			
			<div class="form-actions">
				<button id="submitBtn" type="submit"
					class="btn btn-primary active nav">Reserve Now</button>
			</div>
		</fieldset>
	</form:form>
	</div>
	
	<script  src="js/formFunctions.js" type="text/javascript"> </script>