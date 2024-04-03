<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="../../../css/bootstrap.min.css">
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

        <%--CREATE POST FORM--%>
        <div class="col-6">
            <div class="card border-light">
                <div class="card-body">
                    <h1 class="card-title">Create a post</h1>
                    <c:url var="postUrl" value="/post"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="newPostForm">
                        <div class="mb-3">
                            <label for="titleInput" class="form-label">Title</label>
                            <form:input path="title" class="form-control" id="titleInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="bodyInput" class="form-label">Body</label>
                            <form:textarea path="body" class="form-control" id="bodyInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3 d-flex">
                        <div class="me-4">
                            <label for="bodyInput" class="form-label">Community</label>
                            <form:select itemValue="${communities}" name="community" path="community"
                                         class="form-select" id="specialtiesSelect">
                                <option disabled selected hidden>Choose a community</option>
                                <c:forEach var="community" items="${communities}">
                                    <option value="<c:out value="${community.name}" escapeXml="true" />">
                                        <c:out value="${community.name}" escapeXml="true"/>
                                    </option>
                                </c:forEach>
                            </form:select>
                        </div>
                        <div >
                            <label for="bodyInput" class="form-label">Category</label>
                            <form:select itemValue="${categories}" name="category" path="category"
                                         class="form-select" id="specialtiesSelect">
                                <option disabled selected hidden>Choose a category</option>
                                <c:forEach var="category" items="${categories}">--%>
                                    <option value="<c:out value="${category}" escapeXml="true" />">
                                        <c:out value="${category}" escapeXml="true"/>
                                    </option>
                                </c:forEach>
                            </form:select>
                        </div>
                        </div>
                        <button type="submit" class="btn btn-primary">Create</button>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
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
<script lang="javascript">
    $('.dropdown-toggle').dropdown();

</script>

