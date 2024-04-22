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
</head>
<body>

<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid min-vh-100">
    <div class="row">
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

        <%--CREATE POST FORM--%>
        <div class="col-8">
            <div class="card border-light">
                <div class="card-body">
                    <div class="mb-3 row">
                        <div class="col-2">
                            <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                 class="w-100 h-100 rounded-1" id="imgFile" alt="Profile Picture">

                        </div>
                        <div class="col-8">

                            <h1 class="card-title"><spring:message code="User.Profile"/></h1>
                            <div class="d-flex">
                                <div class="me-5">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Register.Email"/></label>
                                    <p><c:out value="${user.email}" escapeXml="true"/></p>
                                </div>
                                <div>
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Login.Username"/></label>
                                    <p><c:out value="${user.username}" escapeXml="true"/></p>
                                </div>

                            </div>
                            <div>
                                <label class="form-label fw-semibold">Update profile picture</label>
                                <input class="form-control w-50" placeholder="Update profile picture" type="file" id="formFile"
                                       onchange="document.getElementById('imgFile').src = window.URL.createObjectURL(this.files[0])">
                            </div>

                        </div>
                    </div>
                    <h2 class="text-center mt-4">Your posts</h2>
                    <c:if test="${empty posts}">
                        <h4>You don't have posts yet</h4>
                        <c:url value="/" var="homeUrl"/>
                        <a href="${homeUrl}" class="btn btn-primary"><spring:message code="Post.Create"/></a>
                    </c:if>
                    <c:forEach var="post" items="${posts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link text-decoration-none">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="title-container">
                                        <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                             class="small-profile-pic mb-1" alt="Profile Picture">
                                        <p class="fw-semibold card-subtitle">/<c:out value="${post.community_name}"
                                                                                     escapeXml="true"/></p>
                                        <span class="badge rounded-pill mb-1 ${post.category}">${post.category}</span>
                                    </div>

                                    <h4 class="card-title fw-bold"><c:out value="${post.title}" escapeXml="true"/></h4>
                                    <p class="card-text post-body-home"><c:out value="${post.body}"
                                                                               escapeXml="true"/></p>
                                    <p class="m-auto">
                                        <small class="text-body-secondary">
                                            <c:out value="${post.date.format(format)}" escapeXml="true"/>
                                        </small>
                                    </p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <%--POSTS LIST OF THE COMMUNITY--%>
        <div class="col-2">
            <div class="card  border-0">
                <div class="card-body">
                    <h3 class="card-title text-center mb-3">Liked posts</h3>
                    <c:if test="${empty likedPosts}">
                        <h5 class="text-center mt-4">You haven't liked any post yet</h5>
                    </c:if>
                    <c:forEach var="post" items="${likedPosts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link text-decoration-none">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="title-container">
                                        <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                             class="small-profile-pic mb-1" alt="Profile Picture">
                                        <p class="fw-semibold card-subtitle">/<c:out value="${post.community_name}"
                                                                                     escapeXml="true"/></p>
                                        <span class="badge rounded-pill mb-1 ${post.category}">${post.category}</span>
                                    </div>

                                    <h4 class="card-title fw-bold"><c:out value="${post.title}" escapeXml="true"/></h4>
                                    <p class="card-text post-body-home"><c:out value="${post.body}"
                                                                               escapeXml="true"/></p>
                                    <p class="m-auto">
                                        <small class="text-body-secondary">
                                            <c:out value="${post.date.format(format)}" escapeXml="true"/>
                                        </small>
                                    </p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
<script lang="javascript">
    $('.dropdown-toggle').dropdown();

</script>

