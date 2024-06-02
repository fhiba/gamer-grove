<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: juani
  Date: 4/19/2024
  Time: 5:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="col-2 sidebar">
    <div class="card sidebar-card m-auto">
        <div class="card-body">
            <jsp:useBean id="isAdmin" scope="request" type="java.lang.Boolean"/>
            <c:if test="${isAdmin}">
                <div class="d-flex flex-column justify-content-center align-items-center">
                    <c:url value="/manageMods" var="addModUrl"/>
                    <a href="${addModUrl}" class="w-100">
                        <button class="btn btn-outline-primary mb-2 w-100"><spring:message code="Mod.Manage"/></button>
                    </a>
                    <c:url value="/new-community" var="newCommunityUrl"/>
                    <a href="${newCommunityUrl}" class="w-100">
                        <button class="btn btn-outline-success w-100"><spring:message code="Community.Add"/> </button>
                    </a>
                </div>
            </c:if>
            <hr>
            <div class="d-flex flex-column">

                    <c:url value="/home" var="homeUrl"/>
                <a id="home_anchor" href="${homeUrl}" class="fs-5  text-light sidebar-nav">
                   <spring:message code="Navbar.Home"/>
                </a>
                <hr>
                    <c:url value="/all" var="allUrl"/>
                <a id="all_anchor" href="${allUrl}" class=" fs-5 text-light link sidebar-nav">
                    <spring:message code="All"/>
                </a>
            </div>
<hr class="mt-3">
            <jsp:useBean id="isLogged" scope="request" type="java.lang.Boolean"/>
            <c:if test="${!isLogged}">
                <div class="h5 card-title text-light mb-3"><spring:message code="Communities.Title"/></div>
            </c:if>
            <c:if test="${isLogged}">
                <div class="card-title text-light mb-3"><spring:message code="Communities.Logged"/></div>
            </c:if>

            <jsp:useBean id="communities" scope="request" type="java.util.List"/>
            <c:forEach var="community" items="${communities}">
                <c:url value="/community/${community.encodedName}" var="communityUrl"/>
                <a href="${communityUrl}" class="text-light text-decoration-none">
                    <div class="card-body-community d-flex align-items-center text-decoration-none mb-3">
                        <div class="d-flex justify-content-start">
                            <c:if test="${empty community.portrait}">
                                <img src="${pageContext.request.contextPath}/images/default-community.png"
                                     class="very-small-profile-pic mb-1" alt="Profile Picture">
                            </c:if>
                            <c:if test="${not empty community.portrait}">
                                <img src="<c:url value='/image/${community.portrait.imageId}'/>"
                                     class="very-small-profile-pic mb-1" alt="Profile Picture">
                            </c:if>
                        </div>
                        <div class="text-decoration-none">
                            <h5 class="fw-semibold fs-6 card-subtitle text-break truncate-1-lines">
                                /<c:out value="${community.name}" escapeXml="true"/>
                            </h5>
                        </div>
                    </div>
                </a>
            </c:forEach>
            <c:url value="/communities" var="communitiesUrl"/>

            <c:if test="${empty communities}">
                <p><spring:message code="Navbar.NoCommunitiesFollowed"/></p>
            </c:if>
            <a href="${communitiesUrl}" class=" fs-6 card-title text-light mb-3">
                <p><spring:message code="Navbar.AllCommunities"/></p>
            </a>
        </div>
    </div>
</div>
<script>
    let url = window.location.href;
    let home = document.getElementById("home_anchor");
    let all = document.getElementById("all_anchor");
    if (url.includes("home")) {
        home.classList.add("fw-bold");
        all.classList.add("text-decoration-none");
    } else if (url.includes("all") || url.endsWith('/')) {
        all.classList.add("fw-bold");
        home.classList.add("text-decoration-none");
    } else {
        home.classList.add("text-decoration-none");
        all.classList.add("text-decoration-none");
    }
</script>