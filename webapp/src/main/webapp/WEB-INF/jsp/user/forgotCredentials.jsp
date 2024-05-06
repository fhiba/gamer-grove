<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="ForgotCredentials.Title"/></title>
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
                    <h5 class="fw-bold mb-2"><spring:message code="ForgotCredentials.Subtitle"/></h5>
                    <c:url var="forgotPassword" value="/auth/forgotCredentials"/>
                    <form:form action="${forgotPassword}" method="post" modelAttribute="emailForm">
                        <div class="mb-3">
                            <form:input type="email"  class="form-control" name="email" path="email"/>
                        </div>
                        <button class="btn btn-primary" type="submit" value="Register">
                            <spring:message code="ForgotCredentials.Action"/>
                        </button>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
