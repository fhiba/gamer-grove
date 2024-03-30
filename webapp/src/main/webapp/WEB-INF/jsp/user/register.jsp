<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>REGISTER:</h1>
<c:url var="registerUrl" value="/register" />
<form:form action="${registerUrl}" method="post" modelAttribute="registerForm">
    <table>
        <tr>
            <td>Username:</td>
            <td><form:input path="username" /></td>
            <td><form:errors path="username" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><form:password path="password" /></td>
            <td><form:errors path="password" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Confirm Password:</td>
            <td><form:password path="repeatPassword" /></td>
           <td> <form:errors path="repeatPassword" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Email:</td>
            <td><form:input path="email" /></td>
            <td><form:errors path="email" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Register" /></td>
        </tr>
    </table>
    <form:errors cssStyle="color: red" cssClass="error" />
</form:form>
</body>
</html>