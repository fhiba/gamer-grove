<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="card  bg-transparent border-0">
    <div class="card-title news-title">
        <h5><spring:message code="Home.News"/></h5>
    </div>
    <div class="card-body ">
        <jsp:useBean id="news" scope="request" type="java.util.List"/>
        <c:forEach var="a_new" items="${news}">
            <c:url value="/post/${a_new.id}" var="newsUrl"/>
            <a href="${newsUrl}" class="card-link text-decoration-none ">
                <div class="card mb-3">
                    <div class="card-body">
                        <h6 class="card-subtitle text-secondary fw-bold"><c:out
                                value="${a_new.communityName}" escapeXml="true"/></h6>
                        <h5 class="card-title fw-bold"><c:out value="${a_new.title}" escapeXml="true"/></h5>
                        <p class="card-text post-body truncate-4-lines"><c:out value="${a_new.body}" escapeXml="true"/></p>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
</div>