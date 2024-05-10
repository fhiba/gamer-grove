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
<div class="container-fluid min-vh-100">
    <div class="row min-vh-100 justify-content-between">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card m-auto">
                <div class="card-body">
                    <c:if test="${isAdmin}">
                        <div class="row-cols-2">
                            <c:url value="/addMod" var="addModUrl"/>
                            <a href="${addModUrl}">
                                <button class="btn btn-outline-primary">Add Mod</button>
                            </a>
                            <c:url value="/new-community" var="newCommunityUrl"/>
                            <a href="${newCommunityUrl}">
                                <button class="btn btn-outline-success">Add Community</button>
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
                        <c:url value="/community/${community.encodedName}" var="communityUrl"/>
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
        <div class="col-5">
            <div class="card border-0 w-100 ms-5">
                <div class="card-body">
                    <h3 class="fw-semibold card-subtitle mb-1">
                        <spring:message code="Mod.New"/>
                    </h3>
                    <c:url var="addModUrl" value="/addMod"/>
                    <form:form action="${addModUrl}" method="post" modelAttribute="newModForm">
                        <div class="mt-2">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Login.Username"/>: </label>
                            <form:input path="username"/>
                            <form:errors path="username" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mt-2">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Mod.Communnity"/>: </label>
                            <form:select cssClass="text-bg-dark" path="communityId" id="addModSelect">
                                <c:forEach var="community" items="${communities}">
                                    <form:option cssStyle="color:black;" value="${community.id}"
                                                 label="${community.name}"/>
                                </c:forEach>
                            </form:select>
                            <form:errors path="communityId" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <button class="btn btn-outline-primary mt-2"><spring:message code="Mod.Add"/></button>
                    </form:form>
                </div>
            </div>
        </div>
        <div class="col-5">
            <div class="card border-0 w-100 ms-5">
                <div class="card-body">
                    <h3 class="fw-semibold card-subtitle mb-1">
                        <spring:message code="Mod.Remove"/>
                    </h3>
                    <c:url var="removeModUrl" value="/removeMod"/>
                    <form:form action="${removeModUrl}" method="post" modelAttribute="removeModForm">
                        <div class="mt-2">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Login.Username"/>: </label>
                            <form:input path="removeUsername"/>
                            <form:errors path="removeUsername" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mt-2">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Mod.Communnity"/>: </label>
                            <form:select class="mod-form bg-dark" path="fromCommunityId" id="removeModSelect">
                                <c:forEach var="community" items="${communities}">
                                    <form:option value="${community.id}" label="${community.name}"/>
                                </c:forEach>
                            </form:select>
                        </div>
                        <form:errors path="fromCommunityId" cssStyle="color: red" cssClass="error"/>
                        <button class="btn btn-outline-danger mt-2"><spring:message code="Mod.Remove"/></button>
                    </form:form>
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
    <div class="modal fade" id="alreadyModModal" tabindex="-1" aria-labelledby="alreadyModModalLabel"
         aria-hidden="true">
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
</div>
</body>
<script>
    $(document).ready(function () {
        $('#addModSelect').select2({
            placeholder: "Select a community",
            allowClear: true,
            color: "black!important",
        });
    });
    $(document).ready(function () {
        $('#removeModSelect').select2({
            placeholder: "Select a community",
            color: "black",
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