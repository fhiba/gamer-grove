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
    <%--suppress JSUnresolvedLibraryURL --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <%--suppress JSUnresolvedLibraryURL --%>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-beta.1/dist/css/select2.min.css" rel="stylesheet"/>
    <%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-beta.1/dist/js/select2.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
            <div class="col-2 sidebar">
                <div class="card sidebar-card m-auto">
                    <div class="card-body">
                        <c:if test="${isAdmin}">
                            <div class="row-cols-2">
                                <c:url value="/addMod" var="addModUrl"/>
                                <a href="${addModUrl}">
                                    <button class="btn-outline-primary">Add Mod</button>
                                </a>
                                <c:url value="/new-community" var="newCommunityUrl"/>
                                <a href="${newCommunityUrl}">
                                    <button class="btn-outline-primary">Add Community</button>
                                </a>
                            </div>
                        </c:if>
                        <hr>
                        <c:url value="/home" var="homeUrl"/>
                        <a href="${homeUrl}" class="text-decoration-none card-title text-light mb-3">
                            <h5><spring:message code="Navbar.Home"/></h5>
                        </a>
                        <hr>

                        <c:url value="/all" var="allUrl"/>
                        <a href="${allUrl}" class="text-decoration-none card-title text-light mb-3">
                            <h5><spring:message code="All"/></h5>
                        </a>
                        <hr>
                        <c:if test="${isLogged == null}">
                            <div class="h5 card-title text-light mb-3">Communities</div>
                        </c:if>
                        <c:if test="${isLogged != null}">
                            <div class="card-title text-light mb-3">My Communities</div>
                        </c:if>
                        <c:forEach var="community" items="${communities}">
                            <c:url value="/community/${community.name}" var="communityUrl"/>
                            <a href="${communityUrl}" class="text-light text-decoration-none">
                                <div class="card-body-community d-flex align-items-center text-decoration-none mb-3">
                                    <c:if test="${community.portrait_id == 0}">
                                        <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                             class="very-small-profile-pic mb-1" alt="Profile Picture">
                                    </c:if>
                                    <c:if test="${community.portrait_id != 0}">
                                        <img src="<c:url value='/image/${community.portrait_id}'/>"
                                             class="very-small-profile-pic mb-1" alt="Profile Picture">
                                    </c:if>
                                    <div class="text-decoration-none">
                                        <h5 class="fw-semibold card-subtitle ">
                                            /<c:out value="${community.name}" escapeXml="true"/>
                                        </h5>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>

        <div class="col-6 mt-3">
            <div>
                <div class="card border-dark-subtle bg-dark-subtle d-inline-flex col">
                    <div class="card-body ">
                        <c:url value="/community/${communityName}/image" var="fileUrl"/>
                        <form:form method="POST" action="${fileUrl}" enctype="multipart/form-data"
                                   modelAttribute="newCommunityImage">
                            <table>

                                <tr>
                                    <form:input type="hidden" value="${communityName}" name="communityName"
                                                path="communityName"/>
                                </tr>
                                <tr>
                                    <td><spring:message code="UploadImage.Select"/></td>
                                    <td><form:input type="file" name="file" path="file"/></td>
                                </tr>

                                <tr>
                                    <td><input type="submit" value="Submit"/></td>
                                </tr>
                            </table>
                            <form:errors path="file" cssStyle="color: red"/>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
</body>
</html>
