<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Communities</title>
</head>
<body>
    <c:url value="/community" var="getCommunityUrl"/>
    <!--
        <!c:url value="media/community/portrait" var="portraitUrl"/>
        -->
    <c:url var="portraitUrl" value="https://img.freepik.com/psd-gratis/marcos-fotos-maqueta_53876-57736.jpg"/>
    <c:if test="${empty communities}">
        <div>No communities found</div>
    </c:if>

            <table>
                    <c:forEach items="${communities}" var="community">
                <tr>
                    <td>
                        <img width="50px" height="50px" src="${portraitUrl}" alt="image of a portrait"/>
                        <!--<img src="${portraitUrl}/${community.id}" alt="portrait of community: '${community.name}'"/>-->
                    </td>
                    <td>
                        <a href="${getCommunityUrl}/${community.id}">${community.name}</a>
                    </td>
                </tr>
                    </c:forEach>
            </table>
</body>
</html>
