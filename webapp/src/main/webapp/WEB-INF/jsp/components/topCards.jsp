<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: juani
  Date: 5/11/2024
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="card  bg-transparent border-0">
    <div class="card-title news-title">
        <h5><spring:message code="Home.Top"/></h5>
    </div>
    <div class="card-body ">
        <jsp:useBean id="topPost" scope="request" type="java.util.List"/>
        <c:forEach var="a_top" items="${topPost}">
            <c:url value="/post/${a_top.id}" var="topUrl"/>
            <a href="${topUrl}" class="card-link text-decoration-none ">
                <div class="card mb-3">
                    <div class="card-body">
                        <h6 class="card-subtitle text-secondary fw-bold"><c:out
                                value="${a_top.communityName}" escapeXml="true"/></h6>
                        <h5 class="card-title fw-bold"><c:out value="${a_top.title}" escapeXml="true"/></h5>
                        <p class="card-text post-body truncate-4-lines"><c:out value="${a_top.body}" escapeXml="true"/></p>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
</div>