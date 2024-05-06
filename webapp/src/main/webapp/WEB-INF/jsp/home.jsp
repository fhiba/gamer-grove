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
    <%--suppress JSUnresolvedLibraryURL --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">

</head>
<body>
<%@ include file="components/header.jsp" %>
<div class="container-fluid">
    <div class="row  min-vh-100">
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
                                <button class="ms-2 btn btn-outline-success">Add Community</button>
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
                                <div class="d-flex justify-content-start">
                                    <c:if test="${community.portrait_id == 0}">
                                        <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                             class="very-small-profile-pic mb-1" alt="Profile Picture">
                                    </c:if>
                                    <c:if test="${community.portrait_id != 0}">
                                        <img src="<c:url value='/image/${community.portrait_id}'/>"
                                             class="very-small-profile-pic mb-1" alt="Profile Picture">
                                    </c:if>
                                </div>
                                <div class="text-decoration-none">
                                    <h5 class="fw-semibold card-subtitle text-break">
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
        <div class="col-5">
            <div class="card border-0">
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
                    <c:if test="${posts.size() == 0}">
                        <c:url var="allUrl" value="/all"/>
                        <div class="align-items-center text-center justify-content-center w-100">
                                <h6 class="card-title"><spring:message code="Home.NoPosts"/> <a href="${allUrl}"><spring:message code="Home.GoAll"/></a></h6>

                        </div>
                    </c:if>
                    <c:forEach var="post" items="${posts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link text-decoration-none">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="title-container">
                                            <%--                                        <c:if test="${community.portrait_id == 0}">--%>
                                            <%--                                            <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"--%>
                                            <%--                                                 class="very-small-profile-pic mb-1" alt="Profile Picture">--%>
                                            <%--                                        </c:if>--%>
                                            <%--                                        <c:if test="${community.portrait_id != 0}">--%>
                                            <%--                                            <img src="<c:url value='/image/${community.portrait_id}'/>"--%>
                                            <%--                                                 class="very-small-profile-pic mb-1" alt="Profile Picture">--%>
                                            <%--                                        </c:if>                                            --%>
                                        <p class="fw-semibold card-subtitle">/<c:out value="${post.communityName}"
                                                                                     escapeXml="true"/></p>
                                        <span class="badge rounded-pill mb-1 ${post.category}">${post.category}</span>
                                    </div>
                                    <c:if test="${!post.deleted}">
                                        <div>
                                            <h4 class="card-title fw-bold"><c:out value="${post.title}"
                                                                                  escapeXml="true"/></h4>
                                            <p class="card-text post-body"><c:out value="${post.body}"
                                                                                  escapeXml="true"/></p>
                                        </div>
                                        <div>

                                        </div>
                                    </c:if>
                                    <c:if test="${post.deleted}">
                                        <h4 class="card-title fw-bold"><spring:message code="Post.Deleted"/></h4>
                                        <p class="card-text post-body"><<spring:message code="Post.Deleted"/></p>
                                    </c:if>
                                    <div class="d-flex row-cols-2 justify-content-between me-5 mt-1">
                                        <p class="w-100">
                                            <small class="text-body-secondary">
                                                <c:out value="${post.date.format(format)}" escapeXml="true"/>
                                            </small>
                                        </p>
                                        <p><c:out value="${post.grooviness}" escapeXml="true"/></p>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-1"></div>
        <%--LISTA DE NEWS--%>
        <div class="col-3 mt-5">
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
                                    <h6 class="card-subtitle text-secondary fw-bold"><c:out
                                            value="${a_new.communityName}" escapeXml="true"/></h6>
                                    <h5 class="card-title fw-bold"><c:out value="${a_new.title}" escapeXml="true"/></h5>
                                    <p class="card-text post-body"><c:out value="${a_new.body}" escapeXml="true"/></p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>

            <div id="toastBox" class=" position-fixed bottom-0 end-0 m-3" style="display: none">
                <div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                    <div class="toast-header">
                        <strong id="toast_header" class="me-auto"></strong>
                        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                    <div class="toast-body text-dark" id="toast_body">

                    </div>
                </div>
            </div>
            <c:if test="${isLogged && !isVerified }">
                <c:url value="auth/resend-verification" var="verifyUrl"/>
                <div class="toast show position-fixed bottom-0 end-0 m-3" role="alert" aria-live="assertive" aria-atomic="true" id="verifyToastBox">
                    <div class="toast-body text-dark">
                        <spring:message code="VerifyAccount.Verify"/>
                        <div class="mt-2 pt-2 border-top">
                            <a href="${verifyUrl}"><button type="button" class="btn btn-primary btn-sm"><spring:message code="VerifyAccount.Resend"/></button></a>
                            <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="toast"><spring:message code="Close"/></button>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>


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



    let hasToast = document.URL.includes("verifySuccess");
    const successMessage = "<spring:message code="VerifyAccount.Success"/>";
    const errorMessage = " <spring:message code="VerifyAccount.Error"/>";
    if(hasToast) {
        let toastMessage = document.URL.split("verifySuccess=")[1];
        console.log(toastMessage);
        document.getElementById('toast_header').innerText = "<spring:message code="Toast.Title.Notification"/>";
        document.getElementById('toast_body').innerText = toastMessage === 'true' ? successMessage : errorMessage;
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
    let hasRegistered = document.URL.includes("registerSuccess");
    if(hasRegistered) {
        document.getElementById('verifyToastBox').style.display = 'none';
        document.getElementById('toast_header').innerText = "<spring:message code="Toast.Title.Welcome"/>";
        document.getElementById('toast_body').innerText = "<spring:message code="Register.Success"/>";
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
    let resendVerification = document.URL.includes("resendVerification");

    if(resendVerification) {

        document.getElementById('verifyToastBox').style.display = 'none';
        document.getElementById('toast_header').innerText = "<spring:message code="Toast.Title.Notification"/>";
        document.getElementById('toast_body').innerText = "<spring:message code="VerifyAccount.EmailSent"/>";
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
</script>

