<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" session="true"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>Ticketing System</title>

<spring:url value="/" var="baseUri" />
<spring:url value="/resources/bootstrap/css/bootstrap.min.css"
	var="bootstrapUrl" />
<link href="${bootstrapUrl}" rel="stylesheet">

<spring:url value="/resources/jquery/jquery-1.11.0.min.js"
	var="jqueryUrl" />
<script src="${jqueryUrl}"></script>
<spring:url value="/resources/img/" var="imgUrl" />
<spring:url value="/" var="baseUri" />
<style type="text/css">
fieldset {
	z-index: 1000;
}
</style>
</head>
<body>
	<div class="container">
		<!--  HEADER -->
		<c:import url="/WEB-INF/views/fragments/header.jsp" charEncoding="UTF-8" />
		<!--  PAGE  -->
		<c:import url="/WEB-INF/views/fragments/${content}.jsp" charEncoding="UTF-8" />
		<!-- FOOTER -->
		<c:import url="/WEB-INF/views/fragments/footer.jsp" charEncoding="UTF-8" />
	</div>
</body>
</html>