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

                <div class="mb-3 d-flex">
                    <c:url var="clearUsernameFilterUrl" value="/manageMods">
                        <c:forEach var="entry" items="${param}">
                            <c:if test="${!entry.key.equals('username')}">
                                <c:param name="${entry.key}" value="${entry.value}"/>
                            </c:if>
                        </c:forEach>
                    </c:url>
                    <c:url var="clearCommunityFilterUrl" value="/manageMods">
                        <c:forEach var="entry" items="${param}">
                            <c:if test="${!entry.key.equals('community')}">
                                <c:param name="${entry.key}" value="${entry.value}"/>
                            </c:if>
                        </c:forEach>
                    </c:url>
                    <div class="input-manage-mods input-group-sm d-flex me-2" id="filterUsernameDiv">
                        <a href="${clearUsernameFilterUrl}" id="clearButtonUsernameFilter" >

                        <button class="btn btn-outline-secondary btn-sm input-manage-mods">
                            <i class="fa fa-x" aria-hidden="true"></i>
                        </button>
                        </a>
                        <input  id="filterByUsername" type="text" class="form-control small" placeholder="<spring:message code="Mod.FilterByUsername"/>"  aria-describedby="button-addon2">
                        <button class="btn btn-outline-secondary" type="button" id="button-addon2" onclick="filterMods()"><spring:message code="Mod.Filter"/></button>
                    </div>
                    <a href="${clearCommunityFilterUrl}" id="clearButtonCommunityFilter" >
                        <button class="btn btn-outline-secondary btn-sm input-manage-mods">
                            <i class="fa fa-x" aria-hidden="true"></i>
                        </button>
                    </a>
                    <select onchange="filterMods()" class="form-select" aria-label="Default select example"
                            id="filterByCommunities">
                        <option selected disabled><spring:message code="Mod.FilterByCommunity"/></option>
                        <c:forEach items="${allCommunities}" var="communty">
                            <option>${communty.name}</option>
                        </c:forEach>
                    </select>
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
                <c:if test="${not empty moderator}">
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
                </c:if>
                </tbody>
            </table>
            <c:if test="${empty modders.data && empty moderator}">
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
                                <option selected disabled><spring:message code="Mod.SelectCommunity"/></option>
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
<script>
    // Function to replace a substring with another substring
    function replaceSubstring(originalString, substringToFind, substringToReplace) {
        // Use the replace method to find and replace the substring
        const newString = originalString.replace(substringToFind, substringToReplace);
        return newString;
    }

    // Example usage
    const originalString = document.URL;
    const substringToFind = "addMod";
    const substringToReplace = "manageMods";

    // Call the function
    const result = replaceSubstring(originalString, substringToFind, substringToReplace);

    console.log(new URL(result));
    console.log(new URL(document.URL))
    const filterMods = () => {
        let url = replaceSubstring(document.URL, "addMod", "manageMods");
        let community = document.getElementById('filterByCommunities').value;
        let username = document.getElementById('filterByUsername').value;
        let newUrl = new URL(url);
        let defaultValue = "<spring:message code="Mod.FilterByCommunity" />";
        if (community !== undefined && community !== '' && community !== defaultValue) {
            newUrl.searchParams.set('community', community);
            newUrl.searchParams.set('pageNumber', '1');
        }
        if (username !== undefined && username !== '') {
            newUrl.searchParams.set('username', username);
            newUrl.searchParams.set('pageNumber', '1');
        }
        window.location = newUrl.href;
    }

    $(document).ready(function () {
        $('#addModSelect').select2({
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
        document.getElementById('clearButtonCommunityFilter').style.display = 'none'
    }
    let username = urlParams.get('username');
    if(username){
        document.getElementById('filterByUsername').value = username;
    }else{
        document.getElementById('clearButtonUsernameFilter').style.display = 'none'
    }
</script>
</html>