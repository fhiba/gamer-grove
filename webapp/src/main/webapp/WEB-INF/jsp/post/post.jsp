<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${post.title}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid h-100">
    <div class="row mt-4">
        <%--COMMUNITY LIST--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Community</h5>
                            <p class="card-text">Some quick example to build on the card title and make up the
                                bulk of the card's content.</p>
                        </div>
                    </div>
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Community</h5>
                            <p class="card-text">Some quick example to build on the card title and make up the
                                bulk of the card's content.</p>
                        </div>
                    </div>
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Community</h5>
                            <p class="card-text">Some quick example to build on the card title and make up the
                                bulk of the card's content.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%--POST DATA--%>
        <div class="col-6">
            <div class="card border-light">
                <div class="card-body">
                    <p class="fw-semibold card-subtitle mb-1">
                        /community
                        <span class="badge rounded-pill text-bg-primary pb-2">category</span>
                    </p>
                    <h5 class="card-title mb-0">Post title</h5>
                    <p class="card-subtitle mb-4">username</p>

                    <p class="card-text">This is a wider card with supporting text below as a natural lead-in to
                        additional content. This content is a little bit longer.This is a wider card with supporting text below as a natural lead-in to
                        additional content. This content is a little bit longer.This is a wider card with supporting text below as a natural lead-in to
                        additional content. This content is a little bit longer.This is a wider card with supporting text below as a natural lead-in to
                        additional content. This content is a little bit longer.This is a wider card with supporting text below as a natural lead-in to
                        additional content. This content is a little bit longer.</p>
                    <p class="card-text"><small class="text-body-secondary">Last updated 3 mins ago</small></p>
                </div>
            </div>
        </div>
        <%--POSTS LIST OF THE COMMUNITY--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Post title</h5>
                            <p class="card-text">Some quick example to build on the card title and make up the
                                bulk of the card's content.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</body>
</html>
