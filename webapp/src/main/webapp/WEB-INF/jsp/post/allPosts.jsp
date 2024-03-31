<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<c:url value="/all-posts" var="postsUrl"/>

    <label for="category"></label><select id="category" onclick="filterPosts()">
        <option value="all">All</option>
        <c:forEach var="category" items="${categories}">
            <option value="${category}">${category}</option>
       </c:forEach>

    </select>
<c:forEach var="post" items="${posts}">
    <div >
        <table>
            <tr>
                <td>${post.title}</td>
                <td>${post.category}</td>
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
<script>
const filterPosts = () => {
    let url = document.URL;
    console.log(url);
    let category = document.getElementById('category').value;
    let newUrl = new URL(url);
    newUrl.searchParams.set('category', category);

    window.location.search = newUrl.search;
}
</script>