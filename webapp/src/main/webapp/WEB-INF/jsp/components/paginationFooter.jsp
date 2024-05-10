<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paginatedDataWrapper" scope="request" type="ar.edu.itba.paw.models.pagination.PaginatedDataWrapper"/>
<%--
  Created by IntelliJ IDEA.
  User: juani
  Date: 5/9/2024
  Time: 8:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:if test="${paginatedDataWrapper.pageNumber  < paginatedDataWrapper.totalPages && paginatedDataWrapper.pageNumber >= 1 && paginatedDataWrapper.totalPages > 1}">
    <nav aria-label="Page navigation example">
        <ul class="pagination">
            <c:if test="${paginatedDataWrapper.pageNumber != 1}">
                <c:url var="firstPageUrl" value="?pageNumber=1"/>
                <li class="page-item"><a class="page-link" href="${firstPageUrl}">First</a></li>
                <c:url var="prevPageUrl" value="?pageNumber=${paginatedDataWrapper.pageNumber -1}"/>
                <li class="page-item"><a class="page-link" href="${prevPageUrl}"> Prev </a></li>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber == 1}">
                <c:url var="prevPageUrl" value="?pageNumber=${paginatedDataWrapper.pageNumber -1}"/>
                <li class="page-item"><a class="page-link disabled" href="${prevPageUrl}"> Prev </a></li>
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
                <c:url var="pageUrl" value="?pageNumber=${i}"/>
                <c:if test="${i == paginatedDataWrapper.pageNumber}">
                    <li class="page-item"><a class="page-link active" href="${pageUrl}"><c:out value="${i}"/></a></li>
                </c:if>
                <c:if test="${i != paginatedDataWrapper.pageNumber}">
                    <li class="page-item"><a class="page-link" href="${pageUrl}"><c:out value="${i}"/></a></li>
                </c:if>
            </c:forEach>
            <c:if test="${paginatedDataWrapper.pageNumber == paginatedDataWrapper.totalPages}">
            <c:url var="nextPageUrl" value="?pageNumber=${paginatedDataWrapper.pageNumber+1}"/>
                <li class="page-item"><a class="page-link disabled">Next</a></li>
            </c:if>
            <c:if test="${paginatedDataWrapper.pageNumber != paginatedDataWrapper.totalPages}">
                <c:url var="nextPageUrl" value="?pageNumber=${paginatedDataWrapper.pageNumber+1}"/>
                <li class="page-item"><a class="page-link" href="${nextPageUrl}">Next</a></li>
                <c:url var="lastPageUrl" value="?pageNumber=${paginatedDataWrapper.totalPages}"/>
                <li class="page-item"><a class="page-link" href="${lastPageUrl}">Last</a></li>
            </c:if>
        </ul>
    </nav>
</c:if>