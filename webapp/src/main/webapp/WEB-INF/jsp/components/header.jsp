<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="searchTerms" scope="request" class="java.lang.String"/>
<nav class="navbar">
    <div class="container-fluid">
        <div class="row w-100">
            <div class="col-3">
                <c:url value="/" var="homeUrl"/>
                <a class="navbar-brand d-flex align-items-end" href="${homeUrl}">

<%--suppress CheckImageSize --%>
                    <img width="50" height="50" alt="logo" src="${pageContext.request.contextPath}/images/favicon.ico"/>
                    <span class="h3 text-light mb-1">Gamer Grove</span>
                </a>

            </div>
            <div class="col-6 d-flex align-items-center justify-content-center">
                <c:url value="/communities" var="communitySearch"/>
                <form class="d-flex align-items-center justify-content-center m-auto w-100 ms-3" role="search"
                      action="${communitySearch}" method="get" id="searchForm">
                    <input class="form-control me-2" type="search" name="searchTerms"
                           placeholder="Search for communities"
                           id="searchTerms"
                           aria-label="Search"
                    <c:if test="${not empty searchTerms}">
                        value="${searchTerms}"
                    </c:if>
                    >

                    <button class="btn btn-outline-success" type="submit" id="searchButton"><spring:message
                            code="Navbar.Search"/></button>
                </form>
            </div>
            <c:if test="${not empty pageContext.request.userPrincipal}">
            <div class="col-3 d-flex justify-content-end align-items-center">
<%--suppress XmlPathReference --%>
                <c:url value="/logout" var="logoutUrl"/>
                <a class="btn btn-outline-danger " href="${logoutUrl}"><spring:message code="Logout"/></a>
            </div>
            </c:if>
            <c:if test="${empty pageContext.request.userPrincipal}">
                <div class="col-3 d-flex justify-content-end align-items-center">
                    <c:url value="/login" var="loginUrl"/>
                    <a class="btn btn-outline-primary " href="${loginUrl}"><spring:message code="Navbar.Login"/></a>
                </div>
            </c:if>
        </div>
    </div>
</nav>