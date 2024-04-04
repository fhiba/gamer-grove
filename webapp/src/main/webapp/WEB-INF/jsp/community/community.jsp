<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${community.name}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js" rel="stylesheet"/>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<!-- Button to trigger modal -->
<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createPostModal">
    Create Post
</button>

<!-- Modal -->
<div class="modal fade" id="createPostModal" tabindex="-1" aria-labelledby="createPostModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="createPostModalLabel">Create a Post</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="mb-3">
                        <label for="postTitle" class="form-label">Title</label>
                        <input type="text" class="form-control" id="postTitle" placeholder="Enter post title">
                    </div>
                    <div class="mb-3">
                        <label for="postContent" class="form-label">Content</label>
                        <textarea class="form-control" id="postContent" rows="5" placeholder="Enter post content"></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="postCategory" class="form-label">Category</label>
                        <select class="form-select" id="postCategory">
                            <option selected>Select category...</option>
                            <option value="1">Category 1</option>
                            <option value="2">Category 2</option>
                            <option value="3">Category 3</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Create Post</button>
            </div>
        </div>
    </div>
</div>
<div class="container-fluid">
    <div class="row mt-4">
        <div class="col-3">
        </div>
        <div class="col-6">
            <h1 class="card-title">${community.name}</h1>
            <h5 class="card-subtitle text-secondary">${community.description}</h5>
            <div class="card  border-light">
                <div class="card-body">
                    <div class="d-flex justify-content-between ">
                        <c:url value="/post" var="newPostUrl"/>
                        <a href="${newPostUrl}" type="button" class="btn  btn-primary  h-25 me-2 mt-1">Create post</a>
                    </div>
                    <c:forEach var="post" items="${posts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link link-underline-light">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <p class="fw-semibold card-subtitle mb-1">
                                        /${post.community_name}
                                        <span class="badge rounded-pill text-bg-primary pb-2">${post.category}</span>
                                    </p>
                                    <h4 class="card-title">${post.title}</h4>
                                    <p class="card-text post-body text-secondary post-body">${post.body}</p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-3">
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</body>
</html>
<script>
    let postBody = document.getElementsByClassName('post-body');
    console.log(postBody.length);
    for (let i = 0; i < postBody.length; i++) {
        if (postBody[i].innerText.length > 100) {
            postBody[i].innerText = postBody[i].innerText.substring(0, 100) + '...';
        }
    }
</script>