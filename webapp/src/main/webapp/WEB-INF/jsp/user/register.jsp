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
        <div class="col-6">
            <div class="card mb-4">
                <div class="card-body">
                    <h2 class="fw-bold mb-2 text-uppercase"><spring:message code="Register.Title"/></h2>
                    <c:url var="registerUrl" value="/register"/>
                    <form:form action="${registerUrl}" method="post" modelAttribute="registerForm">
                        <div class="mb-3">
                            <label for="titleInput" class="form-label"><spring:message code="Login.Username"/></label>
                            <form:input path="username" class="form-control" id="titleInput"/>
                            <form:errors path="username" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="titleInput" class="form-label"><spring:message code="Login.Password"/></label>
                            <form:password path="password" class="form-control" id="titleInput"/>
                            <form:errors path="password" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="titleInput" class="form-label"><spring:message
                                    code="Register.RepeatPassword"/></label>
                            <form:password path="repeatPassword" class="form-control" id="titleInput"/>
                            <form:errors path="repeatPassword" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="titleInput" class="form-label"><spring:message code="Register.Email"/></label>
                            <form:input path="email" class="form-control" id="titleInput"/>
                            <form:errors path="email" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <button class="btn btn-primary" type="submit" value="Register">
                            <spring:message code="Register.Title"/>
                        </button>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</div>
</body>
</html>