<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="Register.Title"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js" rel="stylesheet"/>
</head>
<body>
<section class="vh-100 gradient-custom">
    <div class="container py-5 h-100">
        <div class="row d-flex justify-content-center align-items-center h-100">
            <div class="col-12 col-md-8 col-lg-6 col-xl-5">
                <div class="card bg-dark text-white" style="border-radius: 1rem;">
                    <div class="card-body p-5 text-center">

                        <div class="mb-md-5 mt-md-4 pb-5">

                            <h2 class="fw-bold mb-2 text-uppercase"><spring:message code="Register.Title"/></h2>
                            <c:url var="registerUrl" value="/register"/>
                            <form:form action="${registerUrl}" method="post" modelAttribute="registerForm">
                                <div class="form-outline form-white mb-4">
                                    <i><form:input path="username"/></i>
                                    <i><form:errors path="username" cssStyle="color: red" cssClass="error"/></i>
                                    <label class="form-label" for="username"><spring:message code="Login.Username"/></label>
                                </div>

                                <div class="form-outline form-white mb-4">
                                    <i><form:password path="password"/></i>
                                    <i><form:errors path="password" cssStyle="color: red" cssClass="error"/></i>
                                    <label class="form-label" for="password"><spring:message code="Login.Password"/></label>
                                </div>

                                <div class="form-outline form-white mb-4">
                                    <i><form:password path="repeatPassword"/></i>
                                    <i> <form:errors path="repeatPassword" cssStyle="color: red" cssClass="error"/></i>
                                    <label class="form-label" for="repeatPassword"><spring:message code="Register.RepeatPassword"/></label>
                                </div>

                                <div class="form-outline form-white mb-4">
                                    <i><form:input path="email"/></i>
                                    <i><form:errors path="email" cssStyle="color: red" cssClass="error"/></i>
                                    <label class="form-label" for="repeatPassword">Email</label>
                                </div>

                                <button class="btn btn-outline-light btn-lg px-5" type="submit" value="Register">
                                    <spring:message code="Register.Title"/>
                                </button>
                                <form:errors cssStyle="color: red" cssClass="error"/>
                            </form:form>
                            <div class="d-flex justify-content-center text-center mt-4 pt-1">
                                <a class="text-white"><i class="fab fa-facebook-f fa-lg"></i></a>
                                <a class="text-white"><i class="fab fa-twitter fa-lg mx-4 px-2"></i></a>
                                <a class="text-white"><i class="fab fa-google fa-lg"></i></a>
                            </div>

                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>