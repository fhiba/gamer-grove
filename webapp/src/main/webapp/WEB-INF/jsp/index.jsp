<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" />
    <title>Gamer Groove</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<h2><c:out value="${message}" escapeXml="true"/>!</h2>
<c:url value="/all-posts" var="allPostsUrl" />
<a href="${allPostsUrl}">All Posts</a>
<script href="/js/bootstrap.bundle.min.js" ></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</body>
</html>