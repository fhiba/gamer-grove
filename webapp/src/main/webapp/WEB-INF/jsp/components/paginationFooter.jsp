<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:useBean id="paginatedDataWrapper" scope="request" type="ar.edu.itba.paw.models.pagination.PaginatedDataWrapper"/>
<jsp:useBean id="pageNumberName" scope="request" type="java.lang.String"/>

<%--
  Created by IntelliJ IDEA.
  User: juani
  Date: 5/9/2024
  Time: 8:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:if test="${not empty paginatedDataWrapper&&paginatedDataWrapper.pageNumber  <= paginatedDataWrapper.totalPages && paginatedDataWrapper.pageNumber >= 1 && paginatedDataWrapper.totalPages > 1}">
    <nav aria-label="Page navigation example">
        <ul class="pagination">
            <c:if test="${paginatedDataWrapper.pageNumber != 1}">
                <c:url var="firstPageUrl" value="">
                    <c:param name="${pageNumberName}" value="1"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <li class="page-item"><a class="page-link" href="${firstPageUrl}"><<</a></li>
                <c:url var="prevPageUrl" value="">
                    <c:param name="${pageNumberName}" value="${paginatedDataWrapper.pageNumber -1}"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <li class="page-item"><a class="page-link" href="${prevPageUrl}"> < </a></li>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber == 1}">
                <c:url var="prevPageUrl" value="">
                    <c:param name="${pageNumberName}" value="${paginatedDataWrapper.pageNumber -1}"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <li class="page-item"><a class="page-link disabled" href="${prevPageUrl}"> < </a></li>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber == paginatedDataWrapper.totalPages}">
                <c:set var="endLoop" value="${paginatedDataWrapper.pageNumber}"/>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber + 1 == paginatedDataWrapper.totalPages}">
                <c:set var="endLoop" value="${paginatedDataWrapper.pageNumber + 1}"/>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber + 2 <= paginatedDataWrapper.totalPages}">
                <c:set var="endLoop" value="${paginatedDataWrapper.pageNumber + 2}"/>
            </c:if>


            <c:forEach begin="${paginatedDataWrapper.pageNumber}" end="${endLoop}" var="i">
                <c:url var="pageUrl" value="">
                    <c:param name="${pageNumberName}" value="${i}"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <c:if test="${i == paginatedDataWrapper.pageNumber}">
                    <li class="page-item"><a class="page-link active" href="${pageUrl}"><c:out value="${i}"/></a>
                    </li>
                </c:if>
                <c:if test="${i != paginatedDataWrapper.pageNumber}">
                    <li class="page-item"><a class="page-link" href="${pageUrl}"><c:out value="${i}"/></a></li>
                </c:if>
            </c:forEach>
            <c:if test="${paginatedDataWrapper.pageNumber == paginatedDataWrapper.totalPages}">
                <c:url var="nextPageUrl" value="">
                    <c:param name="${pageNumberName}" value="${paginatedDataWrapper.pageNumber +1}"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <li class="page-item"><a class="page-link disabled"> > </a></li>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber != paginatedDataWrapper.totalPages}">
                <c:url var="nextPageUrl" value="">
                    <c:param name="${pageNumberName}" value="${paginatedDataWrapper.pageNumber +1}"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <li class="page-item"><a class="page-link" href="${nextPageUrl}"> > </a></li>
                <c:url var="lastPageUrl" value="">
                    <c:param name="${pageNumberName}" value="${paginatedDataWrapper.totalPages}"/>
                    <c:forEach var="entry" items="${param}">
                        <c:if test="${!entry.key.equals(pageNumberName)}">
                            <c:param name="${entry.key}" value="${entry.value}"/>
                        </c:if>
                    </c:forEach>
                </c:url>
                <li class="page-item"><a class="page-link" href="${lastPageUrl}"> >> </a></li>
            </c:if>
        </ul>
    </nav>
</c:if>