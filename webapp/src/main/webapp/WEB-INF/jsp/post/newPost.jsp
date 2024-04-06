<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js" rel="stylesheet"/>
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
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5 class="card-title">${community.name}</h5>
                                <p class="card-text">${community.description}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <%--CREATE POST FORM--%>
        <div class="col-6">
            <div class="card border-light">
                <div class="card-body">
                    <h1 class="card-title"><spring:message code="Post.Create"/> </h1>
                    <c:url var="postUrl" value="/post"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="newPostForm">
                        <div class="mb-3">
                            <label for="titleInput" class="form-label"><spring:message code="Post.Title"/></label>
                            <form:input path="title" class="form-control" id="titleInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="bodyInput" class="form-label"><spring:message code="Post.Body"/></label>
                            <form:textarea path="body" class="form-control" id="bodyInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3 d-flex">
                        <div class="me-4">
                            <label for="bodyInput" class="form-label"><spring:message code="Post.Community"/></label>
                            <form:select  name="community" path="community"
                                         class="form-select" id="specialtiesSelect">
                                <option disabled selected hidden><spring:message code="Post.ChooseCommunity"/></option>
                                <c:forEach var="community" items="${communities}">
                                    <option value="<c:out value="${community.name}" escapeXml="true" />">
                                        <c:out value="${community.name}" escapeXml="true"/>
                                    </option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="community" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div >
                            <label for="bodyInput" class="form-label"><spring:message code="Post.Category"/></label>
                            <form:select  name="category" path="category"
                                         class="form-select" id="specialtiesSelect">
                                <option disabled selected hidden><spring:message code="Post.ChooseCategory"/></option>
                                <c:forEach var="category" items="${categories}">--%>
                                    <option value="<c:out value="${category}" escapeXml="true" />">
                                        <c:out value="${category}" escapeXml="true"/>
                                    </option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="category" cssStyle="color: red" cssClass="error"/>
                        </div>
                        </div>
                        <button type="submit" class="btn btn-primary"><spring:message code="Post.CreateButton"/></button>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
                </div>
            </div>
        </div>
        <%--POSTS LIST OF THE COMMUNITY--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach items="${news}" var="a_new">
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5 class="card-title">${a_new.title}</h5>
                                <p class="card-text">${a_new.body}</p>
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
    $('.dropdown-toggle').dropdown();

</script>

