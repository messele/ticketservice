<%@ include file="include.jsp"%>
<spring:url value="/" var="holdSeatsUrl"></spring:url>
<div class="jumbotron">

	<c:if test="${not empty error}">
		<div class="alert alert-danger">${error}</div>
	</c:if>
	<c:choose>
		<c:when test="${seatsAvailable > 1 }">
			<form:form modelAttribute="holdSeatsInput" class="form-horizontal"
				id="hold-seats-form" action="${holdSeatsUrl}" method="post">
				<fieldset>
					<div class="form-group">
						<label for="noOfSeatsNeeded" style="padding-right: 15px;">No
							of Seats Needed :</label>
						<form:input class="input" type="text" id="noOfSeatsNeeded"
							path="noOfSeatsNeeded"
							style="padding-left:10px;padding-right:5px;" maxlength="11"
							size="12px" />
						<span class="error"><form:errors path="noOfSeatsNeeded" /></span>
						<p class="help-block">this needs to be an integer in the range
							of 1 to ${seatsAvailable}</p>
					</div>
					<div class="form-group">
						<label for="email" style="padding-right: 5px;">Email :</label>
						<form:input class="input" type="text" path="email" id="email"
							style="padding-left:10px;padding-right:5px;" size="25px" />
						<span class="error"><form:errors path="email" /></span>
						<p class="help-block">this email will be used to hold/reserve
							seats.</p>
					</div>
					<div class="form-actions">
						<button id="submitBtn" type="submit"
							class="btn btn-primary active nav">Submit</button>
						<button id="resetBtn" type="reset" class="btn btn-primary active">Cancel</button>
					</div>
				</fieldset>
			</form:form>
			<script src="resources/js/formFunctions.js" type="text/javascript">
				
			</script>
		</c:when>
		<c:otherwise>
		<div class="alert alert-warning"> no more seats available for reservation.</div>
		</c:otherwise>
	</c:choose>

</div>
