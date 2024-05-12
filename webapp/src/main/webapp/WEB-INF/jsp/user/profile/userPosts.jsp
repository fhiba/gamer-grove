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
                            <c:if test="${user.portraid_id == 0}">

                                <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                     class="w-100 h-100 rounded-1 img-com" id="imgFile" alt="Profile Picture">
                            </c:if>
                            <c:if test="${user.portraid_id != 0}">

                                <img src="<c:url value='/image/${user.portraid_id}'/>"
                                     class="w-100 h-100 rounded-1 img-com" id="imgFile" alt="Profile Picture">
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
                                <c:if test="${!user.isVerified()}">
                                    <div class="ms-5">
                                        <c:url value="auth/resend-verification" var="resendUrl"/>
                                        <spring:message code="VerifyAccount.Verify"/>
                                        <a href="${resendUrl}">
                                            <button type="button" class="p-0 btn btn-link"><spring:message
                                                    code="email.validateAccount.action"/></button>
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                            <div>
                                <c:url value="/user/update" var="userUpdateUrl"/>
                                <form:form method="POST" action="${userUpdateUrl}" enctype="multipart/form-data"
                                           modelAttribute="userPfpForm">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Profile.UpdateProfilePicture"/></label>
                                    <form:input
                                            onchange="document.getElementById('imgFile').src = window.URL.createObjectURL(this.files[0])"
                                            path="file" class="form-control w-50" accept="image/*" type="file"/>
                                    <p class="mt-3 mb-auto"><form:errors path="file" cssStyle="color: red"
                                                                         cssClass="error"/></p>
                                    <div class="w-25">
                                        <label class="form-label fw-semibold">Language</label>
                                        <form:select style="" class="form-select" required="true" path="locale">
                                            <form:option selected="true" hidden="true" value="${user.locale}"> <c:out
                                                    value="${user.locale}" escapeXml="true"/> </form:option>
                                            <form:option value="es">es</form:option>
                                            <form:option value="en">en</form:option>
                                        </form:select>
                                        <form:errors path="locale" cssStyle="color: red" cssClass="error"/>
                                    </div>
                                    <button type="submit" class="btn btn-primary mt-3"><spring:message
                                            code="Update"/></button>
                                    <form:errors cssStyle="color: red" cssClass="error"/>
                                </form:form>
                            </div>

                        </div>
                    </div>
                    <ul class="nav nav-tabs">
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page"><spring:message
                                    code="Profile.UserPosts"/></a>
                        </li>
                        <li class="nav-item">
                            <c:url var="likedPostsUrl" value="/profile/likedPosts"/>
                            <a class="nav-link" href="${likedPostsUrl}"><spring:message code="Profile.LikedPost"/></a>
                        </li>
                    </ul>
                    <c:if test="${empty posts.data}">
                        <div class="mt-5 d-flex justify-content-center">
                            <div class="mb-4 d-flex flex-column align-items-center">
                                <h4><spring:message code="Profile.NoUserPosts"/></h4>
                                <c:url value="/" var="homeUrl"/>
                                <a href="${homeUrl}" class="btn btn-primary w-50"><spring:message code="Post.Create"/></a>
                            </div>
                        </div>
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
                            <c:set var="paginatedDataWrapper" value="${posts}" scope="request"/>
                            <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                            <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
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
    $('.dropdown-toggle').dropdown();

</script>

