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
            <div class="card-title text-light mb-3">Communities</div>

            <jsp:useBean id="communities" scope="request" type="java.util.List"/>
            <c:forEach var="community" items="${communities}">
                <c:url value="/community/${community.name}" var="communityUrl"/>
                <a href="${communityUrl}" class="text-light text-decoration-none">
                    <div class="card-body-community d-flex align-items-center text-decoration-none mb-3">
                        <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                             class="very-small-profile-pic mb-1" alt="Profile Picture">
                        <div class="text-decoration-none">
                            <h5 class="fw-semibold card-subtitle ">
                                /<c:out value="${community.name}" escapeXml="true"/>
                            </h5>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>
</div>