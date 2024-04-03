<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>

</head>
<body>
<%@ include file="components/header.jsp" %>
<div class="container-fluid">
    <div class="row mt-4">
        <%--LISTA DE COMUNIDADES--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach var="post" items="${posts}">
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5 class="card-title">Community</h5>
                                <p class="card-text">Some quick example to build on the card title and make up the
                                    bulk of the card's content.</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <%--LISTA DE POSTS--%>
        <div class="col-6">
            <div class="card  border-light">
                <div class="card-body">
                    <div class="d-flex justify-content-between ">
                        <div class="form-floating w-25 mb-3">
                            <select class="form-select" id="floatingSelect" aria-label="Floating label select example">
                                <option value="all" selected disabled>All</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category}">${category}</option>
                                </c:forEach>
                            </select>
                            <label for="floatingSelect">Category</label>
                        </div>
                        <button type="button" class="btn  btn-primary  h-25 me-2 mt-1">Create post</button>

                    </div>
                    <c:forEach var="post" items="${posts}">
                        <div class="card mb-3">
                            <div class="card-body">
                                <p class="fw-semibold card-subtitle mb-1">
                                    /community
                                    <span class="badge rounded-pill text-bg-primary pb-2">category</span>
                                </p>
                                <h4 class="card-title">Post title</h4>
                                <p class="card-text">Some quick example to build on the card title and make up the
                                    bulk of the card's content.</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        <%--LISTA DE NEWS--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach var="post" items="${posts}">
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5 class="card-title">New</h5>
                                <p class="card-text">Some quick example to build on the card title and make up the
                                    bulk of the card's content.</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>
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


