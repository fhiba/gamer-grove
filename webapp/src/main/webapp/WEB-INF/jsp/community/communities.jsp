<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title><spring:message code="Communities.Title"/> </title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>

<div class="container-fluid">
    <div class="row mt-4 mb-3">
        <%--COMMUNITY LIST--%>
        <div class="col-3">
            <%--                <div class="card  border-light">--%>
            <%--                    <div class="card-body">--%>
            <%--                        <c:forEach var="community" items="${communities}">--%>
            <%--                            <c:url value="community/${community.name}" var="communityUrl"/>--%>
            <%--                            <a href="${communityUrl}" class="card-link link-underline-light">--%>
            <%--                                <div class="card mb-3">--%>
            <%--                                    <div class="card-body">--%>
            <%--                                        <h5 class="card-title">${community.name}</h5>--%>
            <%--                                        <p class="card-text post-body">${community.description}</p>--%>
            <%--                                    </div>--%>
            <%--                                </div>--%>
            <%--                            </a>--%>
            <%--                        </c:forEach>--%>
            <%--                    </div>--%>
            <%--                </div>--%>
        </div>

        <%--LISTA DE COMMUNITIES--%>
        <div class="col-6">
            <div class="card  border-light">
                <div class="card-body">
                    <c:if test="${empty communities}">
                        <div><spring:message code="Communities.NoCommunitites"/></div>
                    </c:if>
                    <c:forEach var="community" items="${communities}">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="card-link link-underline-light">
                            <div class="card mb-3">
                                <div class="card-body d-flex ">
                                    <img src="${pageContext.request.contextPath}/images/profile-picture.jpg" class="medium-profile-pic" alt="Profile Picture">
                                    <div>
                                        <h2 class="fw-semibold card-subtitle mb-1">
                                            /${community.name}
                                        </h2>
                                        <h6 class="card-title text-secondary">${community.description}</h6>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <%--LISTA DE NEWS--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <c:forEach var="a_new" items="${news}">
                        <div class="card mb-3">
                            <div class="card-body">
                                <h5 class="card-title">${a_new.title}</h5>
                                <p class="card-text post-body">${a_new.body}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</div>

</body>
</html>
