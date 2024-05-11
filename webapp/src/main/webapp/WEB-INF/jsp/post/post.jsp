<%--suppress ELSpecValidationInJSP --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><c:out value="${post.title}" escapeXml="true"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <%--suppress JSUnresolvedLibraryURL --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <div class="col-1">
        </div>
        <%--POST DATA--%>
        <div class="col-5">
            <div class="card border-0 bg-transparent">
                <div class="card-body">
                    <p class="fw-semibold card-subtitle mb-1">
                        <c:url value="/community/${post.communityName}" var="communityUrl"/>
                        <a href="${communityUrl}"
                           class="text-decoration-none text-light text-body-primary">c/<c:out
                                value="${post.communityName}"
                                escapeXml="true"/></a>
                        <span class="badge rounded-pill ${post.category}"><c:out value="${post.category}"
                                                                                 escapeXml="true"/></span>
                    </p>

                    <c:if test="${post.deleted}">
                        <h4 class="card-title fw-bold mb-0"><spring:message code="Post.Deleted"/></h4>
                        <p class="card-subtitle mb-4">u/<spring:message code="Post.Anon"/></p>
                        <p>
                            <spring:message code="Post.Deleted"/>
                        </p>
                    </c:if>
                    <c:if test="${!post.deleted}">
                        <h4 class="card-title fw-bold mb-0"><c:out value="${post.title}" escapeXml="true"/></h4>
                        <p class="card-subtitle mb-4">u/<c:out value="${author}" escapeXml="true"/></p>
                        <p class="card-text"><c:out value="${post.body}" escapeXml="true"/></p>
                        <c:if test="${post.images.size() > 0}">
                            <div id="carouselExample" class="carousel slide ">
                                <div class="carousel-inner bg-dark">
                                    <c:forEach var="image" items="${post.images}" varStatus="loop">
                                        <div class="carousel-item  <c:if test="${loop.index == 0}"> active</c:if>">
                                            <img src="<c:url value='/image/${image}'/>" class="d-block m-auto "
                                                 style="height: 500px;width: 500px" alt="...">
                                        </div>
                                    </c:forEach>
                                </div>
                                <button class="carousel-control-prev" type="button"
                                        data-bs-target="#carouselExample"
                                        data-bs-slide="prev">
                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                    <span class="visually-hidden"><spring:message code="Previous"/></span>
                                </button>
                                <button class="carousel-control-next" type="button"
                                        data-bs-target="#carouselExample"
                                        data-bs-slide="next">
                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                    <span class="visually-hidden"><spring:message code="Next"/></span>
                                </button>
                            </div>
                        </c:if>
                        <c:url value="/post/${postId}/delete" var="deletePostUrl"/>
                        <c:if test="${canDelete}">
                            <form:form action="${deletePostUrl}" var="deletePostUrl" method="post"
                                       modelAttribute="postDeleteForm">
                                <form:hidden path="postId" value="${post.id}"/>
                                <button class="btn btn-danger btn-sm" type="submit">
                                    <spring:message code="Post.DeleteButton"/>
                                </button>
                            </form:form>
                        </c:if>
                        <div class="d-flex align-items-center">
                            <p class="card-text mb-0"><small
                                    class="text-body-secondary"><c:out value="${post.date.format(format)}"
                                                                       escapeXml="true"/></small>
                            </p>
                            <span class="grooviness-count" style="margin-left: 1rem;"><c:out value="${post.grooviness}"
                                                                                             escapeXml="true"/></span>
                            <div class="d-none">
                                <c:url value="/post/${postId}/up" var="upPostUrl"/>
                                <form:form action="${upPostUrl}" method="post" modelA="newPostGroovyForm"
                                           modelAttribute="newPostGroovyForm">
                                    <form:hidden path="postId" value="${post.id}"/>
                                    <form:hidden path="groovyType" id="postGroovyType"/>
                                </form:form>
                            </div>
                            <div class="d-flex flex-row mb-1">
                                <c:if test="${isGrooved == 1}">
                                    <button onclick="postGroovyUpdate(true)" class="btn">
                                        <i class="fas fa-arrow-up text-primary"></i>
                                    </button>
                                    <button onclick="postGroovyUpdate(false)" class="btn">
                                        <i class="fas fa-arrow-down"></i>
                                    </button>
                                </c:if>
                                <c:if test="${isGrooved == -1}">
                                    <button onclick="postGroovyUpdate(true)" class="btn">
                                        <i class="fas fa-arrow-up"></i>
                                    </button>
                                    <button onclick="postGroovyUpdate(false)" class="btn">
                                        <i class="fas fa-arrow-down text-danger"></i>
                                    </button>
                                </c:if>
                                <c:if test="${isGrooved != 1 && isGrooved != -1}">
                                    <button onclick="postGroovyUpdate(true)" class="btn">
                                        <i class="fas fa-arrow-up"></i>
                                    </button>

                                    <button onclick="postGroovyUpdate(false)" class="btn">
                                        <i class="fas fa-arrow-down"></i>
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </div>

            </div>
            <%--COMMENTS--%>
            <div class="card bg-body-secondary">
                <div class="card-body">
                    <c:url var="commentUrl" value="/comment"/>
                    <form:form action="${commentUrl}" method="post" modelAttribute="newCommentForm">
                        <div class="form-outline form-white mb-4">
                            <form:textarea path="body" class="w-100 rounded-3 pt-2 ps-2" rows="4"
                                           placeholder="Join the discussion and leave a comment!"/>
                            <form:errors path="body" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <%--suppress XmlDuplicatedId --%>
                        <form:hidden path="postId" value="${post.id}"/>
                        <button class="btn btn-primary" type="submit">
                            <spring:message code="Post.Comment"/>
                        </button>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
                    <div class="d-none">
                        <c:url value="/post/${postId}/+" var="upCommentUrl"/>
                        <form:form action="${upCommentUrl}" method="post" id="upCommentForm"
                                   modelAttribute="newCommentGroovyForm">
                            <form:hidden path="commentId" id="commentId"/>
                            <form:hidden path="commentPostId" value="${post.id}"/>
                            <%--suppress XmlDuplicatedId --%>
                            <form:hidden path="groovyType" id="groovyType"/>
                        </form:form>
                    </div>

                    <ul class="list-group">
                        <c:forEach var="comment" items="${comments.data}">
                            <c:if test="${!comment.deleted}">
                                <li class="list-group-item d-flex justify-content-between align-items-start bg-body-secondary">
                                        <%--suppress CheckImageSize --%>
                                    <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                         height="50" width="50" class="rounded-5" alt="Profile Picture">
                                    <div class="ms-2 me-auto">
                                        <div class="fw-bold"><c:out value="${comment.username}" escapeXml="true"/>
                                        </div>
                                        <p class="text-break">
                                            <c:out value="${comment.body}" escapeXml="true"/>
                                        </p>
                                        <p>
                                            <small class="text-body-secondary">
                                                <c:out value="${comment.date.format(format)}" escapeXml="true"/>
                                            </small>
                                        </p>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <span class="grooviness-count">${comment.grooviness}</span>
                                        <div class="d-flex flex-column">
                                            <c:if test="${upComments.contains(comment)}">
                                                <button onclick="commentGroovyUpdate(true, ${comment.id})" class="btn">
                                                    <i class="fas fa-arrow-up text-primary"></i>
                                                </button>
                                                <button onclick="commentGroovyUpdate(false, ${comment.id})" class="btn">
                                                    <i class="fas fa-arrow-down"></i>
                                                </button>
                                            </c:if>
                                            <c:if test="${downComments.contains(comment)}">
                                                <button onclick="commentGroovyUpdate(true, ${comment.id})" class="btn">
                                                    <i class="fas fa-arrow-up"></i>
                                                </button>
                                                <button onclick="commentGroovyUpdate(false, ${comment.id})" class="btn">
                                                    <i class="fas fa-arrow-down text-danger"></i>
                                                </button>
                                            </c:if>
                                            <c:if test="${!upComments.contains(comment) && !downComments.contains(comment)}">
                                                <button onclick="commentGroovyUpdate(true, ${comment.id})" class="btn">
                                                    <i class="fas fa-arrow-up"></i>
                                                </button>

                                                <button onclick="commentGroovyUpdate(false, ${comment.id})" class="btn">
                                                    <i class="fas fa-arrow-down"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                        <c:url value="/comment/${postId}/delete" var="deleteCommentUrl"/>
                                        <c:if test="${canDelete}">
                                            <form:form action="${deleteCommentUrl}" var="deleteCommenttUrl"
                                                       method="post"
                                                       modelAttribute="commentDeleteForm">
                                                <form:hidden path="commentId" value="${comment.id}"/>
                                                <button class="btn btn-danger btn-sm align-content-center"
                                                        type="submit">
                                                    X
                                                </button>
                                            </form:form>
                                        </c:if>
                                    </div>
                                </li>
                            </c:if>
                            <c:if test="${comment.deleted}">
                                <li class="list-group-item d-flex justify-content-between align-items-start bg-body-secondary">
                                    <div class="ms-2 me-auto">
                                        <div class="fw-bold"><spring:message code="Post.Anon"/>
                                        </div>
                                        <p>
                                            <spring:message code="Comment.Deleted"/>
                                        </p>
                                        <p>

                                        </p>
                                    </div>
                                </li>
                            </c:if>
                        </c:forEach>
                        <div class="d-flex justify-content-center align-items-center">
                            <c:if test="${not empty invalidPageNumber && not empty comments}">
                                <span class="badge bg-danger">Invalid page number</span>
                            </c:if>
                            <c:if test="${empty invalidPageNumber && not empty comments}">
                                <c:set var="paginatedDataWrapper" value="${comments}" scope="request"/>
                                <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                                <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                            </c:if>
                        </div>
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-1">

        </div>
        <%--COMMUNITY INFO--%>
        <div class="col-3 mt-3">
            <div class="card  border-black bg-transparent">
                <div class="card-body">
                    <div class="card-title d-flex row-cols-2 justify-content-between">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="text-decoration-none">
                            <h5 class="card-title text-light fw-bold"><c:out value="${community.name}"
                                                                             escapeXml="true"/></h5>
                        </a>
                        <div class="d-none">
                            <c:url var="followUrl" value="/community/${community.name}/follow"/>
                            <form:form modelAttribute="followCommunityForm" action="${followUrl}" method="post"
                                       id="followForm">
                                <form:hidden path="communityName" value="${community.name}"/>
                                <form:hidden path="communityId" value="${community.id}"/>
                            </form:form>
                        </div>
                        <c:if test="${isFollowing}">
                            <button onClick="follow()"
                                    class="rounded-pill  btn-outline-danger follow-button fw-bold"
                                    id="followButton">Following
                            </button>
                        </c:if>
                        <c:if test="${!isFollowing}">
                            <button onClick="follow()"
                                    class="rounded-pill   btn-outline-danger  follow-button fw-bold"
                                    id="followButton">Follow
                            </button>
                        </c:if>
                    </div>
                    <c:forEach var="category" items="${community.categories}">
                        <span class="cat-badge badge">${category}</span>
                    </c:forEach>
                    <div class="card-subtitle text-body-secondary mt-3">
                        <c:out value="${community.description}" escapeXml="true"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

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


    let commentGroovyUpdate = (updateType, id) => {
        let groovyType = document.getElementById('groovyType');
        let commentId = document.getElementById('commentId');
        groovyType.value = updateType;
        commentId.value = id;
        document.getElementById('upCommentForm').submit();
    }

    let postGroovyUpdate = (updateType) => {
        let postGroovyType = document.getElementById('postGroovyType');
        postGroovyType.value = updateType;
        document.getElementById('newPostGroovyForm').submit();
    }

    let follow = () => {
        document.getElementById('followForm').submit();
    }

</script>
