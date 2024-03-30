<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>NEW POST:</h1>
<c:url var="postUrl" value="/post" />
<form:form action="${postUrl}" method="post" modelAttribute="newPostForm">
    <table>
        <tr>
            <td>Title:</td>
            <td><form:input path="title" /></td>
            <td><form:errors path="title" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Body:</td>
            <td><form:textarea path="body" /></td>
            <td><form:errors path="body" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Post" /></td>
        </tr>
    </table>
    <form:errors cssStyle="color: red" cssClass="error" />
</form:form>
</body>
</html>
