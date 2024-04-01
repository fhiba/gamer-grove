<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" />

</head>
<body>
<%@ include file="../components/header.jsp"%>
<c:url value="/all-posts" var="postsUrl"/>

    <label for="category"></label><select id="category" onclick="filterPosts()">
        <option value="all">All</option>
        <c:forEach var="category" items="${categories}">
            <option value="${category}">${category}</option>
       </c:forEach>

    </select>
<div class="flex-column">
    <div>
        <c:url value="/post" var="postUrl"/>
        <a href="${postUrl}" class="button btn-outline-primary">Create Post</a>
    </div>
    <div>
        <c:url value="/new-community" var="communityUrl"/>
        <a href="${communityUrl}" class="button btn-outline-primary">Create Community</a>
    </div>
</div>

<c:forEach var="post" items="${posts}">
    <c:url value="/post/${post.id}" var="postUrl"/>
    <a href="${postUrl}" class="link-dark link-underline-opacity-0">
    <div class="card m-5" style="width: 50rem;">
        <div class="card-body" >
            <h6 class="card-title">${post.community_name}</h6>

                <div class="row align-items-center justify-content-center">
                    <div class="col-sm">
                        <h3 class="card-subtitle">${post.title}</h3>
                    </div>
                    <div class="col-sm">
                        <h5 class="card-title">User ${post.author_id}</h5>
                    </div>
                </div>


                <div class="row">
                    <div class="col-sm">
                        <h6 class="card-subtitle mb-2 text-body-secondary">${post.date}</h6>
                    </div>
                    <div class="col-sm">
                        <h6 class="card-subtitle mb-2 text-body-secondary">${post.category}</h6>
                    </div>
                </div>

            <p class="card-text post-body" id="post-body${post.id}">${post.body}</p>
            <a href="#" class="card-link">upvote</a>
            <a href="#" class="card-link">downvote</a>
        </div>
    </div>
    </a>
</c:forEach>

</body>
</html>
<script lang="javascript">
    const filterPosts = () => {
        let url = document.URL;
        console.log(url);
        let category = document.getElementById('category').value;
        let newUrl = new URL(url);
        newUrl.searchParams.set('category', category);

        window.location.search = newUrl.search;
    }
    let postBody = document.getElementsByClassName('post-body');
    console.log(postBody.length);
    for (let i = 0; i < postBody.length; i++) {
        if (postBody[i].innerText.length > 100) {
            postBody[i].innerText = postBody[i].innerText.substring(0, 100) + '...';
        }
    }
</script>