<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: juani
  Date: 3/30/2024
  Time: 6:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<nav class="navbar navbar-expand-lg ">
    <div class="container-fluid">
        <div class="row w-100">
            <div class="col-3">
                <c:url value="/" var="homeUrl"/>
                <a class="navbar-brand d-flex align-items-end" href="${homeUrl}">
                    <img width="50" height="50" alt="logo" src="${pageContext.request.contextPath}/images/favicon.ico"/>
                    <span class="h2"><spring:message
                            code="GamerGrove"/></span>
                </a>
            </div>
            <div class="col-6 d-flex align-items-center justify-content-center">
                <c:url value="/communities" var="communitySearch"/>
                <form class="d-flex align-items-center justify-content-center m-auto w-100 ms-3" role="search"
                      action="${communitySearch}" method="get">
                    <input class="form-control me-2" type="search" name="searchTerms"
                           placeholder="Search for communities"
                           id="searchTerms"
                           aria-label="Search">
                    <button class="btn btn-outline-success" type="submit"><spring:message
                            code="Navbar.Search"/></button>
                </form>
            </div>
            <div class="col-3 d-flex justify-content-end align-items-center">
                <c:url value="/login" var="loginUrl"/>
                <a class="btn btn-outline-primary h-50" href="${loginUrl}"><spring:message code="Navbar.Login"/></a>
            </div>
        </div>
    </div>
</nav>