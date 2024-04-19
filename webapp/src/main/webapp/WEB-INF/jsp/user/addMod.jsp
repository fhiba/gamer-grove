<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="Mod.Add"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-beta.1/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-beta.1/dist/js/select2.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card">
                <div class="card-body">
                    <c:forEach var="community" items="${communities}">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="card-link text-decoration-none">
                            <div class="card-body-community d-flex align-items-center text-decoration-none mb-3">
                                <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                     class="very-small-profile-pic mb-1" alt="Profile Picture">
                                <div class="text-decoration-none">
                                    <h5 class="fw-semibold card-subtitle community-name">
                                        /<c:out value="${community.name}" escapeXml="true"/>
                                    </h5>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-6">
            <div class="card border-0 bg-transparent">
                <div class="card-body">
                    <p class="fw-semibold card-subtitle mb-1">
                    <h1>NEW Mod:</h1>
                    <c:url var="addModUrl" value="/addMod"/>
                    <form:form action="${addModUrl}" method="post" modelAttribute="newModForm">
                        <table>
                            <tr>
                                <td>Username:</td>
                                <td><form:input path="username"/></td>
                                <td><form:errors path="username" cssStyle="color: red" cssClass="error"/></td>
                            </tr>
                            <tr>
                                <td>Mail:</td>
                                <td><form:input path="email"/></td>
                                <td><form:errors path="email" cssStyle="color: red" cssClass="error"/></td>
                            </tr>
                            <tr>
                                <td>Community:</td>
                                <td>
                                    <form:select path="communityId">
                                        <c:forEach var="community" items="${communities}">
                                            <form:option value="${community.id}" label="${community.name}"/>
                                        </c:forEach>
                                    </form:select>
                                </td>
                                <td><form:errors path="communityId" cssStyle="color: red" cssClass="error"/></td>
                            </tr>
                            <tr>
                                <td><input type="submit" value="Create!"/></td>
                            </tr>
                        </table>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    $(document).ready(function() {
        $('select').select2({
            placeholder: "Select a community",
            allowClear: true
        });
    });
</script>
</html>