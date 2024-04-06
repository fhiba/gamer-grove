<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>GamerGrove</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js" rel="stylesheet"/>

</head>
<body>
<%@ include file="components/header.jsp" %>
<div class="container-fluid">
    <div class="row mt-4">
        <%--COMMUNITY LIST--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach var="community" items="${communities}">
                        <c:url value="community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="card-link link-underline-light">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <h5 class="card-title">${community.name}</h5>
                                    <p class="card-text post-body">${community.description}</p>
                                </div>
                            </div>
                        </a>
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
                            <select class="form-select" id="category" aria-label="Floating label select example"
                                    onchange="filterPosts()">
                                <option disabled selected hidden><spring:message code="Home.FilterCategory"/> </option>
                                <option value="all"><spring:message code="All"/></option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category}">${category}</option>
                                </c:forEach>
                            </select>
                            <label for="category"><spring:message code="Home.Category"/></label>
                        </div>
                        <c:url value="/post" var="newPostUrl"/>
                        <a href="${newPostUrl}" type="button" class="btn  btn-primary  h-25 me-2 mt-1"><spring:message code="Post.Create"/></a>
                    </div>
                    <c:forEach var="post" items="${posts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link link-underline-light">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <p class="fw-semibold card-subtitle mb-1">
                                        /${post.community_name}
                                        <span class="badge rounded-pill text-bg-primary">${post.category}</span>
                                    </p>
                                    <h4 class="card-title">${post.title}</h4>
                                    <p class="card-text post-body">${post.body}</p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <%--LISTA DE NEWS--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach var="a_new" items="${news}">
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5 class="card-title">${a_new.title}</h5>
                                <p class="card-text post-body">${a_new.body}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

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
    console.log(postBody.length);
    for (let i = 0; i < postBody.length; i++) {
        if (postBody[i].innerText.length > 100) {
            postBody[i].innerText = postBody[i].innerText.substring(0, 100) + '...';
        }
    }
</script>


