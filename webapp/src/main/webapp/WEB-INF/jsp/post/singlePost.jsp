<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>${post.title}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container">
    <div class="card" style="width: 70rem;">
        <div class="row m-5">
            <div class="col-sm">
                <h1>${post.title}</h1>
            </div>
            <div class="col-sm">
                <h1>User ${post.author_id}</h1>
            </div>
        </div>
        <div class="row">
            <div class="col-sm">
                <p>${post.body}</p>
            </div>
        </div>
        <p class="card-text post-body" id="post-body${post.id}">${post.grooviness}</p>
        <c:url value="/post/${post.id}/1" var="groovyUpUrl"/>
        <a href="#" class="card-link">upvote</a>
        <c:url value="/post/${post.id}/-1" var="groovyDownUrl"/>
        <a href="#" class="card-link">downvote</a>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</body>
</html>
