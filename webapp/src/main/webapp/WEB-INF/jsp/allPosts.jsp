<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<c:forEach var="post" items="${posts}">
    <div >
        <h1>${post.title}</h1>
        <h3>${post.date}</h3>
    </div>
    <p>${post.body}</p>
    <hr>
</c:forEach>
</body>
</html>
