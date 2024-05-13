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
<div id="container" class="container-fluid h-100">
    <div class="row mt-4 justify-content-center">
        <div class="col d-flex justify-content-center">
            <div class="card mb-4 w-25 background-of-card">
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
                    <p><spring:message code="Or"/> </p>
                    <c:url var="registerUrl" value="/register"/>
                    <a href="${registerUrl}" >
                        <spring:message code="Register.Title"/>
                    </a>
                    <br/>
                    <c:url var="forgotPassword" value="/auth/forgotCredentials"/>
                    <a href="${forgotPassword}" class="link-light link-underline-opacity-0">
                        <spring:message code="Login.ForgotPass"/></a>
                </div>
            </div>
        </div>
    </div>
    <div id="toastBox" class=" position-fixed bottom-0 end-0 m-3" style="display: none">
        <div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header">
                <strong class="me-auto"><spring:message code="Toast.Title.Notification"/></strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body text-dark" id="toast_body">
            </div>
        </div>
    </div>
</div>
</body>
<script>
    let hasToast = document.URL.includes("resetPassword");
    const successMessage = "<spring:message code="ForgotCredentials.Success"/>";
    const errorMessage = " <spring:message code="ForgotCredentials.Error"/>";
    if(hasToast) {
        let toastMessage = document.URL.split("resetPassword=")[1];
        console.log(toastMessage);
        document.getElementById('toast_body').innerText = toastMessage === 'true' ? successMessage : errorMessage;
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
    let hasToastSucceeded = document.URL.includes("resetSuccess");
    const resetSuccessMessage = "<spring:message code="ResetPassword.Success"/>";
    const resetErrorMessage = " <spring:message code="ResetPassword.Error"/>";
    if(hasToastSucceeded) {
        let toastMessage = document.URL.split("resetSuccess=")[1];
        console.log(toastMessage);
        document.getElementById('toast_body').innerText = toastMessage === 'true' ? resetSuccessMessage : resetErrorMessage;
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
</script>
</html>