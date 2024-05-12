<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="404"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <%--suppress JSUnresolvedLibraryURL --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>

<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="d-flex justify-content-center align-items-center min-vh-100">
  <img width="400" height="400" alt="GamerGrove logo" src="${pageContext.request.contextPath}/images/favicon.ico"/>
    <div>
        <h1><spring:message code="404"/></h1>
        <p><spring:message code="404.message"/> </p>
        <c:url value="/" var="homeUrl"/>
        <a href="${homeUrl}" class="btn btn-primary"><spring:message code="GoHomeButton"/></a>
    </div>
</div>

</body>
</html>