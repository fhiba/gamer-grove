<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="GamerGrove"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<%@ include file="components/header.jsp" %>
<div class="container-fluid">
    <div class="row mb-3">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card">
                <div class="card-body">
                    <c:forEach var="community" items="${communities}">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="card-link text-decoration-none ">
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
        <div class="col-1"></div>
        <%--LISTA DE POSTS--%>
        <div class="col-6">
            <div class="card  border-light">
                <div class="card-body">
                    <div class="d-flex justify-content-between ">
                        <div class="form-floating w-25 mb-3">
                            <select class="form-select" id="category" aria-label="Floating label select example"
                                    onchange="filterPosts()">
                                <option disabled selected hidden><spring:message code="Home.FilterCategory"/></option>
                                <option value="all"><spring:message code="All"/></option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category}">${category}</option>
                                </c:forEach>
                            </select>
                            <label for="category"><spring:message code="Home.Category"/></label>
                        </div>
                        <c:url value="/post" var="newPostUrl"/>
                        <a href="${newPostUrl}" type="button" class="btn  btn-primary  h-25 me-2 mt-1"><spring:message
                                code="Post.Create"/></a>
                    </div>
                    <c:forEach var="post" items="${posts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link link-underline-light">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="title-container mb-2">
                                        <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                             class="small-profile-pic mb-1" alt="Profile Picture">
                                        <p class="fw-semibold card-subtitle">/<c:out value="${post.community_name}" escapeXml="true"/></p>
                                        <span class="badge rounded-pill ${post.category} mb-1">${post.category}</span>
                                    </div>
                                    <c:if test="${!post.deleted}">
                                    <h4 class="card-title fw-bold"><c:out value="${post.title}" escapeXml="true"/></h4>
                                    <p class="card-text post-body"><c:out value="${post.body}" escapeXml="true"/></p>
                                    </c:if>
                                    <c:if test="${post.deleted}">
                                        <h4 class="card-title fw-bold"><spring:message code="Post.Deleted"/></h4>
                                        <p class="card-text post-body"><<spring:message code="Post.Deleted"/>/></p>
                                    </c:if>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-1"></div>
        <%--LISTA DE NEWS--%>
        <div class="col-2">
            <div class="card  bg-transparent border-0">
                <div class="card-title news-title">
                    <h5><spring:message code="Home.News"/></h5>
                </div>
                <div class="card-body">
                    <c:forEach var="a_new" items="${news}">
                        <c:url value="/post/${a_new.id}" var="newsUrl"/>
                        <a href="${newsUrl}" class="card-link text-decoration-none ">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <h5 class="card-title fw-bold"><c:out value="${a_new.title}" escapeXml="true"/></h5>
                                    <p class="card-text post-body"><c:out value="${a_new.body}" escapeXml="true"/></p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</div>

</body>
</html>
<script lang="javascript">
    const filterPosts = () => {
        let url = document.URL;
        let category = document.getElementById('category').value;
        let newUrl = new URL(url);
        newUrl.searchParams.set('category', category);
        window.location.search = newUrl.search;
    }
    let postBody = document.getElementsByClassName('post-body');
    for (let i = 0; i < postBody.length; i++) {
        if (postBody[i].innerText.length > 100) {
            postBody[i].innerText = postBody[i].innerText.substring(0, 100) + '...';
        }
    }

    let communityName = document.getElementsByClassName('community-name');
    for (let i = 0; i < communityName.length; i++) {
        if (communityName[i].innerText.length > 10) {
            communityName[i].innerText = communityName[i].innerText.substring(0, 10) + '...';
        }
    }
</script>


