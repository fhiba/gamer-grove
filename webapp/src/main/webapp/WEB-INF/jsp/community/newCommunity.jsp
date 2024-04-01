<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<h1>NEW COMMUNITY:</h1>
<c:url var="communityUrl" value="/new-community" />
<form:form action="${communityUrl}" method="post" modelAttribute="newCommunityForm">
    <table>
        <tr>
            <td>Name:</td>
            <td><form:input path="name" /></td>
            <td><form:errors path="name" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Description:</td>
            <td><form:input path="description" /></td>
            <td><form:errors path="description" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Create!" /></td>
        </tr>
    </table>
    <form:errors cssStyle="color: red" cssClass="error" />
</form:form>
</body>
</html>
