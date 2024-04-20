<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${error_title}</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</head>
<body>

<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="d-flex justify-content-center align-items-center min-vh-100">
  <img width="400" height="400" alt="GamerGrove logo" src="${pageContext.request.contextPath}/images/favicon.ico"/>
    <div>
        <h1 ><c:out value="${error_title}"/></h1>
        <p><c:out value="${error_message}"/> </p>
    </div>
</div>

</body>
</html>
