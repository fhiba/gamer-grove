<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${post.title}</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid h-100">
    <div class="row mt-4">
        <%--COMMUNITY LIST--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach var="community" items="${communities}">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="text-decoration-none text-body-primary link-underline-light">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <h5 class="card-title">${community.name}</h5>
                                    <p class="card-text">${community.description}</p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>

        <%--POST DATA--%>
        <div class="col-6">
            <div class="card border-light">
                <div class="card-body">
                    <p class="fw-semibold card-subtitle mb-1">
                        <c:url value="/community/${post.community_name}" var="communityUrl"/>
                        <a href="${communityUrl}"
                           class="text-decoration-none text-body-primary">c/${post.community_name}</a>
                        <span class="badge rounded-pill ${post.category}">${post.category}</span>
                    </p>
                    <h4 class="card-title fw-bold mb-0">${post.title}</h4>
                    <p class="card-subtitle mb-4">u/${author}</p>

                    <p class="card-text">${post.body}</p>
                    <p class="card-text"><small class="text-body-secondary">${post.date.format(format)}</small></p>
                </div>
            </div>
        </div>
        <%--POSTS LIST OF THE COMMUNITY--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach items="${posts}" var="otherPost">
                    <c:url value="/post/${otherPost.id}" var="postUrl"/>
                    <a href="${postUrl}" class="text-decoration-none text-body-primary">
                        <div class="card mb-3">
                            <div class="card-body">

                                <h5 class="card-title other-post-title fw-bold mb-1">${otherPost.title}
                                </h5>
                                <span class="badge rounded-pill ${otherPost.category} mb-1">${otherPost.category}</span>
                                <p class="card-text post-body">${otherPost.body}</p>
                            </div>
                        </div>
                        </c:forEach>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</body>
</html>
<script>
    let postBody = document.getElementsByClassName('post-body');
    for (let i = 0; i < postBody.length; i++) {
        if (postBody[i].innerText.length > 100) {
            postBody[i].innerText = postBody[i].innerText.substring(0, 100) + '...';
        }
    }

    let otherPostTitle = document.getElementsByClassName('other-post-title');
    for (let i = 0; i < otherPostTitle.length; i++) {
        if (otherPostTitle[i].innerText.length > 30) {
            otherPostTitle[i].innerText = otherPostTitle[i].innerText.substring(0, 30) + '...';
        }
    }
</script>
