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
    <script src="https://kit.fontawesome.com/002da5939d.js" crossorigin="anonymous"></script>

</head>

<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid min-vh-100">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${sidebarcommunities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <div class="col-7 d-flex flex-column align-items-start ms-5 mt-5 mb-4">
            <div class="d-flex flex-row w-100 justify-content-between">
                <h3 class="mb-3"><spring:message code="Mod.Modderators"/></h3>
                <div class="mb-3">
                    <select onchange="filterMods()" class="form-select" aria-label="Default select example"
                            id="filterByCommunities">
                        <option selected disabled><spring:message code="Mod.FilterByCommunity"/></option>
                        <c:forEach items="${allCommunities}" var="communty">
                            <option>${communty.name}</option>
                        </c:forEach>
                    </select>
                    <c:url var="manageModUrl" value="/manageMods"/>
                    <a href="${manageModUrl}" id="clearButton">
                        <button class="btn btn-outline-secondary btn-sm">
                            <i class="fa fa-x" aria-hidden="true"></i>
                        </button>
                    </a>

                </div>
            </div>
            <table class="table table-dark background-of-modders w-100">
                <thead>
                <tr>
                    <th class="w-auto" scope="col"><spring:message code="Mod.Username"/></th>
                    <th class="w-auto" scope="col"><spring:message code="Mod.Email"/></th>
                    <th class="w-auto" scope="col"><spring:message code="Post.Community"/></th>
                    <th class="w-auto" scope="col"><spring:message code="Mod.GrantedDate"/></th>
                    <th class="w-auto" scope="col"></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="moderator" items="${modders.data}">
                    <tr>
                        <td><c:out value="${ moderator.user.username}" escapeXml="true"/></td>
                        <td><c:out value="${moderator.user.email}" escapeXml="true"/></td>
                        <td><c:out value="${moderator.community.name}" escapeXml="true"/></td>
                        <td><c:out value="${moderator.sinceDate.format(format)}" escapeXml="true"/></td>
                        <td>
                            <button class="btn btn-outline-danger btn-sm align-content-center"
                                    data-bs-toggle="modal"
                                    data-bs-target="#removeMod${moderator.user.id}${moderator.community.id}Modal">
                                <i class="fas fa-solid fa-trash"></i>
                            </button>
                            <!-- Modal -->
                            <div class="modal fade" id="removeMod${moderator.user.id}${moderator.community.id}Modal"
                                 tabindex="-1"
                                 aria-labelledby="exampleModalLabel"
                                 aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5 modal-title-color"><spring:message
                                                    code="Mod.RemoveConfirmation"/></h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                    aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <h2 class="fs-5"><spring:message code="Mod.Username"/>: <c:out
                                                    value="${moderator.user.username}" escapeXml="true"/></h2>
                                            <h2 class="fs-5"><spring:message code="Post.Community"/> : <c:out
                                                    value="${moderator.community.name}" escapeXml="true"/></h2>
                                        </div>
                                        <div class="modal-footer">
                                            <button data-bs-dismiss="modal" class="btn btn-secondary btn-sm">
                                                <spring:message code="Post.CancelDelete"/>
                                            </button>
                                            <c:url var="removeModUrl" value="/removeMod"/>
                                            <form:form action="${removeModUrl}" method="post"
                                                       modelAttribute="removeModForm">
                                                <form:hidden path="removeUsername" value="${moderator.user.username}"/>
                                                <form:hidden path="fromCommunityId" value="${moderator.community.id}"/>
                                                <button class="btn btn-danger btn-sm" type="submit">
                                                    <spring:message code="Mod.Remove"/>
                                                </button>
                                            </form:form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty modders.data}">
                <h3 class="text-center w-100"><spring:message code="Mod.Empty"/></h3>
            </c:if>
            <c:if test="${not empty modders.data}">
                <div class="d-flex justify-content-center align-items-center">
                    <c:set var="paginatedDataWrapper" value="${modders}" scope="request"/>
                    <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                </div>
            </c:if>
        </div>
        <div class="col-2 d-flex flex-column ms-5 mt-5 mb-4">
            <div class="card" style="background-color: #212529">
                <div class="card-body">
                    <h3 class="card-subtitle mb-4">
                        <spring:message code="Mod.New"/>
                    </h3>
                    <c:url var="addModUrl" value="/addMod"/>
                    <form:form action="${addModUrl}" method="post" modelAttribute="newModForm">
                        <div class="mt-2 d-flex flex-column">
                            <label class="form-label fw-semibold"><spring:message code="Login.Username"/>: </label>
                            <form:input path="username" cssClass="w-75"/>
                            <form:errors path="username" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mt-2 d-flex flex-column ">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Mod.Community"/>: </label>
                            <form:select cssClass="text-bg-dark w-75" path="communityId" id="addModSelect">
                                <c:forEach var="community" items="${allCommunities}">
                                    <form:option cssStyle="color:black;" value="${community.id}"
                                                 label="${community.name}"/>
                                </c:forEach>
                            </form:select>
                            <form:errors path="communityId" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <button class="btn btn-outline-primary mt-3"><spring:message code="Mod.Add"/></button>
                    </form:form>
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
                    <spring:message code="Mod.AlreadyMod"/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<%--REMOVE MOD MAKE MODAL--%>
<%--<div class="card border-0 w-100 ms-5 mt-5">--%>
<%--    <div class="card-body">--%>
<%--        <h3 class="fw-semibold card-subtitle mb-1">--%>
<%--            <spring:message code="Mod.Remove"/>--%>
<%--        </h3>--%>
<%--        <c:url var="removeModUrl" value="/removeMod"/>--%>
<%--        <form:form action="${removeModUrl}" method="post" modelAttribute="removeModForm">--%>
<%--            <div class="mt-2 d-flex flex-column w-25">--%>
<%--                <label class="form-label rounded-3 fw-semibold"><spring:message--%>
<%--                        code="Login.Username"/>: </label>--%>
<%--                <form:input path="removeUsername"/>--%>
<%--                <form:errors path="removeUsername" cssStyle="color: red" cssClass="error"/>--%>
<%--            </div>--%>
<%--            <div class="mt-2 d-flex flex-column w-25">--%>
<%--                <label class="form-label fw-semibold"><spring:message--%>
<%--                        code="Mod.Communnity"/>: </label>--%>
<%--                <form:select class="mod-form bg-dark" path="fromCommunityId" id="removeModSelect">--%>
<%--                    <c:forEach var="community" items="${allCommunities}">--%>
<%--                        <form:option value="${community.id}" label="${community.name}"/>--%>
<%--                    </c:forEach>--%>
<%--                </form:select>--%>
<%--            </div>--%>
<%--            <form:errors path="fromCommunityId" cssStyle="color: red" cssClass="error"/>--%>
<%--            <button class="btn btn-outline-danger mt-3"><spring:message code="Mod.Remove"/></button>--%>
<%--        </form:form>--%>
<%--    </div>--%>
<%--</div>--%>
<script>


    const filterMods = () => {
        let url = document.URL;
        let community = document.getElementById('filterByCommunities').value;
        let newUrl = new URL(url);
        newUrl.searchParams.set('community', community);
        window.location.search = newUrl.search;
    }

    $(document).ready(function () {
        $('#addModSelect').select2({
            placeholder: "Select a community",
            allowClear: true,
            color: "black!important",
        });
    });
    $(document).ready(function () {
        $('#filterByCommunities').select2({
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
    let urlParams = new URLSearchParams(window.location.search);
    let community = urlParams.get('community');
    if (community) {
        document.getElementById('filterByCommunities').value = community;
    } else {
        document.getElementById('clearButton').style.display = 'none'
    }
</script>
</html>