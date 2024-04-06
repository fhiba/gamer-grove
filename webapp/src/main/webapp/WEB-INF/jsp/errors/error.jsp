<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${error_title}</title>
</head>
<body>
    <h1><c:out value="${error_title}"/></h1>
    <p><c:out value="${error_message}"/> </p>
</body>
</html>
