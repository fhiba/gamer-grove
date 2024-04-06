<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: juani
  Date: 3/30/2024
  Time: 6:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
        <c:url value="/" var="homeUrl"/>
        <a class="navbar-brand" href="${homeUrl}" ><img width="60" height="60" href="" alt="" src="${pageContext.request.contextPath}/images/favicon.ico" class=""/></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon" href="${pageContext.request.contextPath}/images/favicon.ico"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <c:url value="/" var="homeUrl"/>
                    <a class="nav-link active" aria-current="page" href="${homeUrl}"><spring:message code="Navbar.Home"/> </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><spring:message code="Navbar.Communities"/></a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                       aria-expanded="false">
                        Dropdown
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#">Action</a></li>
                        <li><a class="dropdown-item" href="#">Another action</a></li>
                        <li>
                            <hr class="dropdown-divider">
                        </li>
                        <li><a class="dropdown-item" href="#">Something else here</a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" aria-disabled="true">Disabled</a>
                </li>
            </ul>
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <c:url value="/communities" var="communitySearch"/>
                <form class="d-flex" role="search" action="${communitySearch}" method="get">
                    <input class="form-control me-2" type="search" name="searchTerms" placeholder="Search"
                           id="searchTerms"
                           aria-label="Search">
                    <button class="btn btn-outline-success" type="submit"><spring:message code="Navbar.Search"/> </button>
                </form>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <c:url value="/login" var="loginUrl"/>
                    <a class="btn btn-outline-success" href="${loginUrl}"><spring:message code="Navbar.Login"/></a>
                </li>
            </ul>
        </div>
    </div>
</nav>