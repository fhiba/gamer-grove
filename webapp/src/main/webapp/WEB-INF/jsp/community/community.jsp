<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>${community.name}</title>
</head>
<body>
    <table>
        <tr>
            <td><h1>${community.name}</h1></td>
        </tr>
        <tr>
            <td><h1>${community.description}</h1></td>
        </tr>
    </table>

            <c:forEach var="post" items="${posts}">
                <td>
                    <table>
                        <tr>
                            <td>${post.title}</td>
                            <td>${post.author_id}</td>
                        </tr>
                        <tr>
                            <td>${post.body}</td>
                        </tr>
                    </table>
                </td>
                <hr>
            </c:forEach>

</body>
</html>
