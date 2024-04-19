<%--
  Created by IntelliJ IDEA.
  User: void
  Date: 19/4/24
  Time: 12:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title><spring:message code="Mod.Remove"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>

</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<h1>NEW COMMUNITY:</h1>
<c:url var="removeModUrl" value="/removeMod" />
<form:form action="${removeModUrl}" method="post" modelAttribute="removeModForm">
    <table>
        <tr>
            <td>Username:</td>
            <td><form:input path="username" /></td>
            <td><form:errors path="username" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Commiunity:</td>
            <td><form:input path="communityId" /></td>
            <td><form:errors path="communityId" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Create!" /></td>
        </tr>
    </table>
    <form:errors cssStyle="color: red" cssClass="error" />
</form:form>
</body>
</html>