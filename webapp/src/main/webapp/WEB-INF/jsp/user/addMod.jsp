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
        <div class="col-1"></div>
        <div class="col-6 mt-3">
            <div>
                <div class="card d-inline-flex col">
                    <div class="card-body ">
                        <p class="fw-semibold card-subtitle mb-1">
                            <spring:message code="Mod.New"/>
                        </p>
                        <c:url var="addModUrl" value="/addMod"/>
                        <form:form action="${addModUrl}" method="post" modelAttribute="newModForm">
                            <table>
                                <tr>
                                    <td><spring:message code="Mod.Username"/></td>
                                    <td><form:input path="username" class="form-control" id="titleInput"/></td>
                                    <td><form:errors path="username" cssStyle="color: red" cssClass="error"/></td>
                                </tr>
                                <tr>
                                    <td><spring:message code="Mod.Email"/></td>
                                    <td><form:input path="email" class="form-control" id="titleInput"/></td>
                                    <td><form:errors path="email" cssStyle="color: red" cssClass="error"/></td>
                                </tr>
                                <tr>
                                    <td><spring:message code="Mod.Communnity"/></td>
                                    <td>
                                        <form:select path="communityId" id="addModSelect">
                                            <c:forEach var="community" items="${communities}">
                                                <form:option cssStyle="color:black;" value="${community.id}"
                                                             label="${community.name}"/>
                                            </c:forEach>
                                        </form:select>
                                    </td>
                                    <td><form:errors path="communityId" cssStyle="color: red" cssClass="error"/></td>
                                </tr>
                                <tr>
                                    <td><input type="submit" value=<spring:message code="Mod.Add"/>/></td>
                                </tr>
                            </table>
                            <form:errors cssStyle="color: red" cssClass="error"/>
                        </form:form>
                    </div>
                </div>
            </div>
            <div class="card mt-3 d-inline-flex col">
                <div class="card-body">
                    <p class="fw-semibold card-subtitle mb-1">
                        <spring:message code="Mod.Remove"/>
                    </p>
                    <c:url var="removeModUrl" value="/removeMod"/>
                    <form:form action="${removeModUrl}" method="post" modelAttribute="removeModForm">
                        <table>
                            <tr>
                                <td><spring:message code="Mod.Username"/></td>
                                <td><form:input path="removeUsername" class="form-control" id="titleInput"/></td>
                                <td><form:errors path="removeUsername" cssStyle="color: red" cssClass="error"/></td>
                            </tr>
                            <tr>
                                <td><spring:message code="Mod.Communnity"/>:</td>
                                <td>
                                        <form:select class="mod-form" path="fromCommunityId" id="removeModSelect">
                                            <c:forEach var="community" items="${communities}">
                                                <form:option value="${community.id}" label="${community.name}"/>
                                            </c:forEach>
                                        </form:select>
                                </td>
                                <td><form:errors path="fromCommunityId" cssStyle="color: red" cssClass="error"/></td>
                            </tr>
                            <tr>
                                <td><input type="submit" value=<spring:message code="Mod.Remove"/>/></td>
                            </tr>
                        </table>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
    <div class="modal fade" id="NotaMod" tabindex="-1" aria-labelledby="alreadyModModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="NotaModLabel">Error</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    This user is not a mod in this community.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="alreadyModModal" tabindex="-1" aria-labelledby="alreadyModModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="alreadyModModalLabel">Error</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                This user is already a mod in this community.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
        $('#addModSelect').select2({
            placeholder: "Select a community",
            allowClear: true,
            theme:"classic"
        });
    });
    $(document).ready(function () {
        $('#removeModSelect').select2({
            placeholder: "Select a community",
            allowClear: true
        });
    });
    $(document).ready(function () {
        <c:if test="${isAlreadyMod}">
        $('#alreadyModModal').modal('show');
        </c:if>
    });
    $(document).ready(function () {
        <c:if test="${notAMod}">
        $('#NotaMod').modal('show');
        </c:if>
    });
</script>
</html>