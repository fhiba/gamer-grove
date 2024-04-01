<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
 <title>Title</title>
    <link rel="stylesheet" type="text/css" href="../../../css/bootstrap.min.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
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
            <td>Category:</td>
            <td>
                <form:select itemValue="${categories}" name="category" path="category" class="mt-5 form-select form-select-lg" id="specialtiesSelect" aria-label="Floating label select example" >
                    <c:forEach var="category" items="${categories}">
                        <option value="<c:out value="${category}" escapeXml="true" />">
                            <c:out value="${category}" escapeXml="true" />
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
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</body>
</html>
<script lang="javascript">
    $('.dropdown-toggle').dropdown();

</script>