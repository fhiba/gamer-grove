<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>/${community.name}</title>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
</head>
<body>
<c:url value="/community/${communityName}/image" var="fileUrl"/>
<form:form method="POST" action="${fileUrl}" enctype="multipart/form-data" modelAttribute="newCommunityImage">
    <table>

        <tr>
            <form:input type="hidden" value="${communityName}" name="communityName" path="communityName"/>
        </tr>
        <tr>
            <td><spring:message code="UploadImage.Select" /></td>
            <td><form:input type="file" name="file" path="file"/></td>
        </tr>

        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
    <form:errors path="file" cssStyle="color: red"/>
</form:form>
</body>
</html>
