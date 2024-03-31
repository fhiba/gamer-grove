<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<c:forEach var="post" items="${posts}">
    <div >
        <table>
            <tr>
                <td>${post.title}</td>
            </tr>
            <tr>
                <td>${post.date}</td>
                <td>${post.community_name}</td>
            </tr>
        </table>
    </div>
    <p>${post.body}</p>
    <hr>
</c:forEach>
</body>
</html>
