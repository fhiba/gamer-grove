<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>${community.name}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
        Launch demo modal
    </button>

    <!-- Modal -->
    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    ...
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary">Save changes</button>
                </div>
            </div>
        </div>
    </div>

</div>
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
<script>
    const myModal = document.getElementById('exampleModal')
    const myInput = document.getElementById('myInput')

    myModal.addEventListener('shown.bs.modal', () => {
        myInput.focus()
    })

</script>
