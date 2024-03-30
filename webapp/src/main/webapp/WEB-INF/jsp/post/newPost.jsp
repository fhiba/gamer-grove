<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
    <title>Title</title>
</head>
<body>
<h1>NEW POST:</h1>
<c:url var="postUrl" value="/post" />
<form:form action="${postUrl}" method="post" modelAttribute="newPostForm">
    <table>
        <tr>
            <td>Title:</td>
            <td><form:input path="title" /></td>
            <td><form:errors path="title" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Body:</td>
            <td><form:textarea path="body" /></td>
            <td><form:errors path="body" cssStyle="color: red" cssClass="error" /></td>
        </tr>

        <tr>
            <td>Community:</td>
            <td>
                <form:select itemValue="${communities}" name="community" path="community" class="mt-5 form-select form-select-lg" id="specialtiesSelect" aria-label="Floating label select example" >
                    <c:forEach var="community" items="${communities}">
                        <option value="<c:out value="${community.name}" escapeXml="true" />">
                            <c:out value="${community.name}" escapeXml="true" />
                        </option>
                    </c:forEach>
                    <label></label>
                </form:select>
            </td>
        </tr>
        <tr>
            <td><input type="submit" value="Post" /></td>
        </tr>
    </table>
    <form:errors cssStyle="color: red" cssClass="error" />
</form:form>
</body>
</html>
<script lang="javascript">
    $('.dropdown-toggle').dropdown();

</script>