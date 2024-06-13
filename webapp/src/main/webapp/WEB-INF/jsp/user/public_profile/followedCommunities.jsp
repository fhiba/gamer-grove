<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="User.Profile"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <%--suppress JSUnresolvedLibraryURL --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>

<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid min-vh-100">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${not empty user}" scope="request"/>
        <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <div class="col-1"></div>
        <%--PROFILE--%>
        <div class="col-8 justify-content-center">
            <div class="card border-light border-0">
                <div class="card-body">
                    <div class="mb-3 row">
                        <div class="col-3">
                            <c:if test="${empty user.image}">

                                <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                     class="w-100 h-100 rounded-1 img-com" id="imgFile" alt="Profile Picture">
                            </c:if>
                            <c:if test="${not empty user.image}">

                                <img src="<c:url value='/image/${user.image.imageId}'/>"
                                     class="w-100 h-100 rounded-1 img-com" id="imgFile" alt="Profile Picture">
                            </c:if>

                        </div>
                        <div class="col-8">
                            <h3 class="card-title"><c:out value="${user.username}" escapeXml="true"/></h3>
                            <div class="d-flex">
                                <div class="me-5">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Register.Email"/></label>
                                    <p><c:out value="${user.email}" escapeXml="true"/></p>
                                </div>
                            </div>
                        </div>
                    </div>
                        <ul class="nav nav-tabs mb-3">
                            <li class="nav-item">
                                <c:url var="userPostsUrl" value="/user/${user.id}/userPosts"/>
                                <a href="${userPostsUrl}" class="nav-link " aria-current="page"><spring:message
                                        code="PublicProfile.Posts"/></a>
                            </li>
                            <li class="nav-item">
                                <c:url var="likedPostsUrl" value="/user/${user.id}/likedPosts"/>
                                <a class="nav-link" href="${likedPostsUrl}"><spring:message
                                        code="PublicProfile.LikedPost"/></a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link active"><spring:message
                                        code="PublicProfile.FollowedCommunities"/></a>
                            </li>
                        </ul>
                    </div>
                    <div class="d-flex row ">
                        <div hidden="hidden">
                            <c:url value="/user/${user.id}/followed" var="followedCommunitiesUrl"/>
                            <form id="filterForm" action="${followedCommunitiesUrl}" method="GET">
                            </form>
                        </div>
                        <div class="col-8">
                            <c:if test="${empty followedCommunities.data}">
                                <c:url value="/communities" var="communitiesUrl"/>
                                <c:choose>
                                    <c:when test="${communities.size() > 0}">
                                        <div class="mt-5 d-flex justify-content-center">
                                            <div class="mb-4 d-flex flex-column align-items-center">
                                                <h6><spring:message code="Profile.NoMatchingCommunities"/></h6>
                                                <form action="${communitiesUrl}" method="get"
                                                      onsubmit="searchCategories()" id="searchWithCategoriesForm">
                                                    <button class="btn btn-primary" type="submit"><spring:message
                                                            code="Profile.findCommunities"/></button>
                                                </form>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="mt-5 d-flex justify-content-center">
                                            <div class="mb-4 d-flex flex-column align-items-center">
                                                <h4><spring:message code="PublicProfile.NoFollowedCommunities"/></h4>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <c:forEach var="community" items="${followedCommunities.data}">
                                <c:url value="/community/${community.encodedName}" var="communityUrl"/>
                                <a href="${communityUrl}" class="card-link text-decoration-none">
                                    <div class="card mb-3">
                                        <div class="card-body d-flex flex-row">
                                            <div class="flex-column">
                                                <c:if test="${empty community.portrait}">
                                                    <img src="${pageContext.request.contextPath}/images/default-community.png"
                                                         class="very-small-profile-pic mb-1" alt="Profile Picture">
                                                </c:if>
                                                <c:if test="${not empty community.portrait}">
                                                    <img src="<c:url value='/image/${community.portrait.imageId}'/>"
                                                         class="very-small-profile-pic mb-1" alt="Profile Picture">
                                                </c:if>
                                            </div>
                                            <div class="flex-column overflow-auto w-100">
                                                <div class="d-flex justify-content-between">
                                                    <h2 class="fw-semibold card-subtitle mb-1">
                                                        /<c:out value="${community.name}" escapeXml="true"/>
                                                    </h2>
                                                    <div class="justify-content-end">
                                                        <c:forEach var="communityCategories"
                                                                   items="${community.category}">
                                                            <span class="fs-6 cat-badge p-1 badge bg-dark-subtle text-dark">${communityCategories.toString()}</span>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                                <h6 class="card-title text-secondary"><c:out
                                                        value="${community.description}" escapeXml="true"/></h6>
                                            </div>
                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                            <div class="d-flex justify-content-center align-items-center">
                                <c:set var="paginatedDataWrapper" value="${followedCommunities}" scope="request"/>
                                <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                                <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                            </div>
                        </div>
                        <c:if test="${not empty followedCommunities.data}">
                            <div class="col-4">
                                <div class="card border-0 text-decoration-none">
                                    <div class="card-body">
                                        <h5><spring:message code="Home.FilterCategory"/></h5>
                                        <div id="categoryPills" class="d-flex flex-row flex-wrap mb-3">
                                        </div>
                                        <div class="d-flex border border-dark-subtle">
                                            <div class="accordion accordion-flush w-100" id="accordionFlushExample">
                                                <div class="accordion-item">
                                                    <h2 class="accordion-header" id="flush-headingOne">
                                                        <button id="addCategoryButton"
                                                                class="accordion-button btn-light collapsed bg-dark text-light"
                                                                type="button"
                                                                data-bs-toggle="collapse"
                                                                data-bs-target="#flush-collapseOne"
                                                                aria-expanded="false" aria-controls="flush-collapseOne">
                                                            <spring:message code="Category.Add"/>
                                                        </button>
                                                    </h2>
                                                    <div id="flush-collapseOne"
                                                         class="accordion-collapse bg-dark collapse border border-top-light justify-content-evenly"
                                                         aria-labelledby="flush-headingOne"
                                                         data-bs-parent="#accordionFlushExample">
                                                        <div id="categoriesBody" class="accordion-body">

                                                            <p class="text-dark-emphasis m-1" hidden="hidden"
                                                               id="emptyCatText">
                                                                <spring:message code="Category.NoMore"/></p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<script lang="javascript">
    const buttonToShowString = "btn btn-outline-dark";
    const buttonToHideString = "btn btn-outline-danger";
    let applyFilterArray = [];
    let selectArray = [];

    let addCategoryToBody = (category) => {
        let body = document.getElementById("categoriesBody");
        let button = document.createElement("button");
        button.id = category + 'Option';
        button.innerHTML = category;
        button.setAttribute("class", "btn btn-outline-light m-1");
        button.onclick = () => {
            addCategory(category);
        };
        body.appendChild(button);
    };
    let addCategory = (category) => {
        //update arrays
        applyFilterArray.push(category);

        addCategoriesToForm();
    }

    let createPill = (selected) => {
        let categoryPills = document.getElementById("categoryPills");
        let pill = document.createElement("div");
        pill.id = selected + 'Pill';
        pill.setAttribute("class", "card flex-row align-items-center border-light justify-content-center m-1 btn p-0");
        let innerDiv = document.createElement("div");
        innerDiv.setAttribute("class", "card-body d-flex flex-row p-2 align-items-center justify-content-center");
        pill.appendChild(innerDiv);
        let p = document.createElement("p");
        p.setAttribute("class", "m-0 me-1");
        p.innerHTML = selected;
        innerDiv.appendChild(p);
        categoryPills.appendChild(pill);
        let removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.setAttribute("class", "btn btn-close btn-close-white flex-col");
        pill.onclick = () => {
            removeCategory(selected);
        };
        innerDiv.appendChild(removeButton);

    }
    let initializeArray = () => {
        <c:forEach var="category" items="${selectedCategories}">
        if ("${category}" !== null && "${category}" !== "") {
            applyFilterArray.push("${category}");
            createPill("${category}");
        }
        </c:forEach>

        <c:forEach var="category" items="${categories}">
        console.log("${category}")
        if (!applyFilterArray.includes("${category}")) {
            selectArray.push("${category}");

            addCategoryToBody("${category}");
        }
        </c:forEach>
        if (selectArray.length === 0) {
            document.getElementById("emptyCatText").hidden = false;
        }
    }
    initializeArray();
    let removeCategory = (category) => {
        applyFilterArray = applyFilterArray.filter(cat => cat !== category);
        addCategoriesToForm();
    }

    let addCategoriesToForm = () => {
        let form = document.getElementById("filterForm");
        let input = document.createElement("input");
        input.type = "hidden";
        input.setAttribute("id", "categories");
        input.name = "categories";
        input.value = applyFilterArray.join(",");
        form.appendChild(input);

        document.getElementById("filterForm").submit();
    }
    let searchCategories = () => {

        let form = document.getElementById("searchWithCategoriesForm");
        let input = document.createElement("input");
        input.type = "hidden";
        input.setAttribute("id", "categories");
        input.name = "categories";
        input.value = applyFilterArray.join(",");
        form.appendChild(input);

        document.getElementById("searchWithCategoriesForm").submit();
    }


    $('.dropdown-toggle').dropdown();

</script>
