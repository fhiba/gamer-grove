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
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <div class="col-1"></div>
        <%--LISTA DE POSTS--%>
        <div class="col-5">
            <div class="card border-0">
                <div class="card-body">
                    <div class="d-flex justify-content-between ">
                        <div class="form-floating w-25 mb-3">
                            <select class="form-select" id="category" aria-label="Floating label select example"
                                    onchange="filterPosts()">
                                <c:if test="${category == null}">
                                    <option selected value="all"><spring:message code="All"/></option>
                                </c:if>
                                <c:if test="${category != null}">
                                    <option value="all"><spring:message code="All"/></option>
                                </c:if>
                                <c:forEach var="categoryItem" items="${categories}">
                                <option <c:if test="${categoryItem == category}"> selected </c:if> value="${categoryItem}">${categoryItem}</option>
                                </c:forEach>
                            </select><label for="category"><spring:message code="Home.Category"/></label>
                        </div>
                        <div class="form-floating w-25 mb-3">
                            <select class="form-select" id="order" aria-label="Floating label select example"
                                    onchange="orderPosts()">
                                <c:forEach var="orderItem" items="${orders}">
                                    <option <c:if test="${orderItem == order}"> selected </c:if> value="${orderItem}"><spring:message code="${orderItem}" /></option>
                                </c:forEach>
                            </select><label for="order"><spring:message code="Home.Order"/></label>
                        </div>

                        <c:url value="/post" var="newPostUrl"/>
                        <a href="${newPostUrl}" type="button" class="btn  btn-primary  h-25 me-2 mt-1"><spring:message
                                code="Post.Create"/></a>
                    </div>
                    <c:if test="${empty posts || posts.data.size() == 0}">
                        <c:url var="allUrl" value="/all"/>
                        <div class="align-items-center text-center justify-content-center w-100">
                                <h6 class="card-title"><spring:message code="Home.NoPosts"/></h6>
                            <a href="${allUrl}"><button type="button" class="btn btn-primary mt-2"><spring:message code="Home.GoAll"/></button></a>
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
                                    <div class="d-flex row-cols-2 justify-content-between mt-1">
                                        <p>
                                            <small class="text-body-secondary">
                                                <c:out value="${post.date.format(format)}" escapeXml="true"/>
                                            </small>
                                        </p>
                                        <div class="d-flex justify-content-end align-items-end">
                                        <span class="badge text-bg-dark pillUpvoteHome bg-transparent border border-light rounded-2 border-1"><c:out value="${post.grooviness}" escapeXml="true"/>
                                                <i class="fa fa-thumbs-up ms-2" aria-hidden="true"></i></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                    <c:if test="${not empty posts}">
                        <div class="d-flex justify-content-center align-items-center">
                                <c:set var="paginatedDataWrapper" value="${posts}" scope="request"/>
                                <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                                <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                        </div>
                    </c:if>
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
                <div class="card-body ">
                    <jsp:useBean id="news" scope="request" type="java.util.List"/>
                    <c:forEach var="a_new" items="${news}">
                        <c:url value="/post/${a_new.id}" var="newsUrl"/>
                        <a href="${newsUrl}" class="card-link text-decoration-none ">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <h6 class="card-subtitle text-secondary fw-bold"><c:out
                                            value="${a_new.communityName}" escapeXml="true"/></h6>
                                    <h5 class="card-title fw-bold"><c:out value="${a_new.title}" escapeXml="true"/></h5>
                                    <p class="card-text post-body truncate-4-lines"><c:out value="${a_new.body}" escapeXml="true"/></p>

                                    <div class="d-flex row-cols-2 justify-content-between mt-1">
                                        <p>
                                            <small class="text-body-secondary">
                                                <c:out value="${a_new.date.format(format)}" escapeXml="true"/>
                                            </small>
                                        </p>
                                        <div class="d-flex justify-content-end align-items-end">
                                        <span class="badge text-bg-dark pillUpvoteHome bg-transparent border border-light rounded-2 border-1"><c:out value="${a_new.grooviness}" escapeXml="true"/>
                                                <i class="fa fa-thumbs-up ms-2" aria-hidden="true"></i></span>
                                        </div>
                                    </div>
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
            <div class="card  bg-transparent border-0">
                <div class="card-title news-title">
                    <h5><spring:message code="Home.Top"/></h5>
                </div>
                <div class="card-body ">
                    <c:forEach var="a_top" items="${topPost}">
                        <c:url value="/post/${a_top.id}" var="newsUrl"/>
                        <a href="${newsUrl}" class="card-link text-decoration-none ">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <h6 class="card-subtitle text-secondary fw-bold"><c:out
                                            value="${a_top.communityName}" escapeXml="true"/></h6>
                                    <h5 class="card-title fw-bold"><c:out value="${a_top.title}" escapeXml="true"/></h5>
                                    <p class="card-text post-body truncate-4-lines"><c:out value="${a_top.body}" escapeXml="true"/></p>

                                    <div class="d-flex row-cols-2 justify-content-between mt-1">
                                        <p>
                                            <small class="text-body-secondary">
                                                <c:out value="${a_top.date.format(format)}" escapeXml="true"/>
                                            </small>
                                        </p>
                                        <div class="d-flex justify-content-end align-items-end">
                                        <span class="badge text-bg-dark pillUpvoteHome bg-transparent border border-light rounded-2 border-1"><c:out value="${a_top.grooviness}" escapeXml="true"/>
                                                <i class="fa fa-thumbs-up ms-2" aria-hidden="true"></i></span>
                                        </div>
                                    </div>
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
        newUrl.searchParams.set('pageNumber','1');
        window.location.search = newUrl.search;
    }
    const orderPosts = () => {
        let url = document.URL;
        let order = document.getElementById('order').value;
        let newUrl = new URL(url);
        newUrl.searchParams.set('order', order);
        newUrl.searchParams.set('pageNumber','1');
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

