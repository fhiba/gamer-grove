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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card">
                <div class="card-body">
                    <c:forEach var="community" items="${communities}">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="card-link text-decoration-none">
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
        <%--POST DATA--%>
        <div class="col-6">
            <div class="card border-0 bg-transparent">
                <div class="card-body">
                    <p class="fw-semibold card-subtitle mb-1">
                        <c:url value="/community/${post.community_name}" var="communityUrl"/>
                        <a href="${communityUrl}"
                           class="text-decoration-none text-body-primary">c/<c:out value="${post.community_name}"
                                                                                   escapeXml="true"/></a>
                        <span class="badge rounded-pill ${post.category}">${post.category}</span>
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
                                    class="text-body-secondary">${post.date.format(format)}</small>
                            </p>
                            <span class="grooviness-count" style="margin-left: 1rem;">${post.grooviness}</span>
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
                            <form:hidden path="groovyType" id="groovyType"/>
                        </form:form>
                    </div>

                    <ul class="list-group">
                        <c:forEach var="comment" items="${comments}">
                            <c:if test="${!comment.deleted}">
                                <li class="list-group-item d-flex justify-content-between align-items-start bg-body-secondary">
                                    <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                         height="50" width="50" class="rounded-5" alt="Profile Picture">
                                    <div class="ms-2 me-auto">
                                        <div class="fw-bold"><c:out value="${comment.username}" escapeXml="true"/>
                                        </div>
                                        <p>
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
                                                <button class="btn btn-danger btn-sm align-content-center"  type="submit">
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
                    </ul>
                </div>
            </div>
        </div>
        <%--POSTS LIST OF THE COMMUNITY--%>
        <div class="col-3">
            <div class="card  border-0 bg-transparent">
                <div class="card-body">
                    <c:forEach items="${posts}" var="otherPost">
                    <c:url value="/post/${otherPost.id}" var="postUrl"/>
                    <a href="${postUrl}" class="text-decoration-none text-body-primary">
                        <div class="card mb-3">
                            <div class="card-body">
                                <c:if test="${!otherPost.deleted}">
                                    <h5 class="card-title card-title other-post-title fw-bold mb-1"><c:out
                                            value="${otherPost.title}"
                                            escapeXml="true"/>
                                    </h5>
                                    <span class="badge rounded-pill ${otherPost.category} mb-1">${otherPost.category}</span>
                                    <p class="card-text post-body"><c:out value="${otherPost.body}"
                                                                          escapeXml="true"/></p>
                                </c:if>
                                <c:if test="${otherPost.deleted}">
                                    <h5 class="card-title
                                 card-title other-post-title fw-bold mb-1"><spring:message code="Post.Deleted"/>
                                    </h5>
                                    <span class="badge rounded-pill ${otherPost.category} mb-1">${otherPost.category}</span>
                                    <p class="card-text post-body"><spring:message code="Post.Deleted"/></p>
                                </c:if>
                            </div>
                        </div>
                        </c:forEach>
                    </a>
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

</script>
