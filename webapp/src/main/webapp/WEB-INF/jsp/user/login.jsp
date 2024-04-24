<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="Register.Title"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>

</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid h-100">
    <div class="row mt-4 justify-content-center">
        <div class="col d-flex justify-content-center">
            <div class="card mb-4 w-25">
                <div class="card-body text-center">
                    <h2 class="fw-bold mb-2"><spring:message code="Login.Title"/></h2>
                    <c:url var="loginUrl" value="/login"/>
                    <form action="${loginUrl}" method="post" modelAttribute="loginForm">
                        <div class="mb-3">
                            <label for="username" class="form-label"><spring:message code="Login.Username"/></label>
                            <input type="text" id="username"  class="form-control" name="username"/>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label"><spring:message code="Login.Password"/></label>
                            <input type="password" id="password" class="form-control" name="password"/>
                        </div>
                        <button class="btn btn-primary" type="submit" value="Register">
                            <spring:message code="Login.Title"/>
                        </button>
                    </form>
                    <c:if test="${error != null}">
                        <p class="text-danger">${error}</p>
                    </c:if>
                    <p>Or</p>
                    <c:url var="registerUrl" value="/register"/>
                    <a href="${registerUrl}" >
                        <spring:message code="Register.Title"/>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>