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
    <div class="row ">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card m-auto">
                <div class="card-body">
                    <c:if test="${isAdmin}">
                        <div class="row-cols-2">
                            <c:url value="/addMod" var="addModUrl"/>
                            <a href="${addModUrl}">
                                <button class="btn btn-outline-primary"><spring:message  code="Mod.Add"/></button>
                            </a>
                            <c:url value="/new-community" var="newCommunityUrl"/>
                            <a href="${newCommunityUrl}">
                                <button class="btn btn-outline-success"><spring:message code="Community.Add"/></button>
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

                    <div class="h5 card-title text-light mb-3"><spring:message code="Communities.Title"/></div>
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
        <%--PROFILE--%>
        <div class="col-8 justify-content-center">
            <div class="card border-light border-0">
                <div class="card-body">
                    <div class="mb-3 row">
                        <div class="col-3">
                            <c:if test="${user.portraid_id == 0}">

                                <img  src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                      class="w-100 h-100 rounded-1" id="imgFile" alt="Profile Picture">
                            </c:if>
                            <c:if test="${user.portraid_id != 0}">

                                <img  src="<c:url value='/image/${user.portraid_id}'/>"
                                      class="w-100 h-100 rounded-1" id="imgFile" alt="Profile Picture">
                            </c:if>

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
                                <c:if test="${!user.isVerified()}" >
                                    <div class="ms-5">
                                        <c:url value="auth/resend-verification" var="resendUrl"/>
                                        <spring:message code="VerifyAccount.Verify"/>
                                        <a href="${resendUrl}"><button type="button" class="p-0 btn btn-link"><spring:message code="email.validateAccount.action"/></button></a>
                                    </div>
                                </c:if>
                            </div>
                            <div>
                                <c:url value="/user/update" var="userUpdateUrl"/>
                                <form:form method="POST" action="${userUpdateUrl}" enctype="multipart/form-data"
                                           modelAttribute="userPfpForm">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Profile.UpdateProfilePicture"/></label>
                                    <form:input onchange="document.getElementById('imgFile').src = window.URL.createObjectURL(this.files[0])" path="file" class="form-control w-50"  type="file" accept="image/*"/>
                                    <p class="mt-3 mb-auto"><form:errors path="file" cssStyle="color: red"
                                                                         cssClass="error"/></p>
                                    <button type="submit" class="btn btn-primary mt-3"><spring:message
                                            code="Update"/></button>
                                    <form:errors cssStyle="color: red" cssClass="error"/>
                                </form:form>
                            </div>

                        </div>
                    </div>
                    <ul class="nav nav-tabs">
                        <li class="nav-item">
                            <c:url var="userPostsUrl" value="/profile/userPosts" />
                            <a class="nav-link" aria-current="page" href="${userPostsUrl}"><spring:message code="Profile.UserPosts"/></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link active" ><spring:message code="Profile.LikedPost"/></a>
                        </li>
                    </ul>
                    <c:if test="${empty posts.data}">
                        <h4><spring:message code="Profile.NoUserPosts"/></h4>
                        <c:url value="/" var="homeUrl"/>
                        <a href="${homeUrl}" class="btn btn-primary"><spring:message code="Post.Create"/></a>
                    </c:if>
                    <c:forEach var="post" items="${posts.data}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link text-decoration-none">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="title-container">
                                        <p class="fw-semibold card-subtitle">/<c:out value="${post.communityName}"
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
                    <div class="d-flex justify-content-center align-items-center">
                        <c:if test="${empty posts}">
                            <span class="badge bg-danger">Invalid page number</span>
                        </c:if>
                        <c:if test="${not empty posts}">
                            <c:set var="paginatedDataWrapper" value="${posts}" scope="request" />
                            <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                            <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>

</body>
</html>
<script lang="javascript">
    $('.dropdown-toggle').dropdown();

</script>
