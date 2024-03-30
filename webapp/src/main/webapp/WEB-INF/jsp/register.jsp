<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>


<section class="vh-100 gradient-custom">
    <div class="container py-5 h-100">
        <div class="row d-flex justify-content-center align-items-center h-100">
            <div class="col-12 col-md-8 col-lg-6 col-xl-5">
                <div class="card bg-dark text-white" style="border-radius: 1rem;">
                    <div class="card-body p-5 text-center">

                        <div class="mb-md-5 mt-md-4 pb-5">

                            <h2 class="fw-bold mb-2 text-uppercase">Register</h2>
                            <c:url var="registerUrl" value="/register" />
                            <form:form action="${registerUrl}" method="post" modelAttribute="registerForm">
                            <div class="form-outline form-white mb-4">
                                <i><form:input path="username" /></i>
                                <i><form:errors path="username" cssStyle="color: red" cssClass="error" /></i>
                                <label class="form-label" for="username">Username</label>
                            </div>

                            <div class="form-outline form-white mb-4">
                                <i><form:password path="password" /></i>
                                <i><form:errors path="password" cssStyle="color: red" cssClass="error" /></i>
                                <label class="form-label" for="password">Password</label>
                            </div>

                            <div class="form-outline form-white mb-4">
                                <i><form:password path="repeatPassword" /></i>
                                <i> <form:errors path="repeatPassword" cssStyle="color: red" cssClass="error" /></i>
                                <label class="form-label" for="repeatPassword">Repeat Password</label>
                            </div>

                            <div class="form-outline form-white mb-4">
                                <i><form:input path="email" /></i>
                                <i><form:errors path="email" cssStyle="color: red" cssClass="error" /></i>
                                <label class="form-label" for="repeatPassword">Email</label>
                            </div>

                            <p class="small mb-5 pb-lg-2"><a class="text-white-50">Forgot password?</a></p>

                            <button class="btn btn-outline-light btn-lg px-5" type="submit" value="Register">Register</button>
                                <form:errors cssStyle="color: red" cssClass="error" />
                            </form:form>
                            <div class="d-flex justify-content-center text-center mt-4 pt-1">
                                <a class="text-white"><i class="fab fa-facebook-f fa-lg"></i></a>
                                <a class="text-white"><i class="fab fa-twitter fa-lg mx-4 px-2"></i></a>
                                <a class="text-white"><i class="fab fa-google fa-lg"></i></a>
                            </div>

                        </div>

                        <div>
                            <p class="mb-0">Don't have an account? <a class="text-white-50 fw-bold">Sign Up</a>
                            </p>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
