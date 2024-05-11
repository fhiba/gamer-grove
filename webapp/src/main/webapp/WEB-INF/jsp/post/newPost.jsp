<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="Post.Create"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://kit.fontawesome.com/002da5939d.js" crossorigin="anonymous"></script>
</head>
<body>

<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid min-vh-100">
    <div class="row min-vh-100 justify-content-between">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>

        <%--CREATE POST FORM--%>
        <div class="col-6">
            <div class="card border-0">
                <div class="card-body">
                    <h1 class="card-title fw-bold"><spring:message code="Post.Create"/></h1>
                    <c:url var="postUrl" value="/post"/>
                    <form:form action="${postUrl}" method="post" modelAttribute="newPostForm"
                               enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="titleInput" class="form-label"><spring:message code="Post.Title"/></label>
                            <form:input path="title" class="form-control" id="titleInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="bodyInput" class="form-label"><spring:message code="Post.Body"/></label>
                            <form:textarea path="body" class="form-control" id="bodyInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3 d-flex justify-content-between">
                            <div>
                                <label for="bodyInput" class="form-label"><spring:message
                                        code="Post.Community"/></label>
                                <form:select name="community" path="community"
                                             class="form-select" id="specialtiesSelect">
                                    <option disabled selected hidden><spring:message
                                            code="Post.ChooseCommunity"/></option>
                                    <c:forEach var="community" items="${communities}">
                                        <option value="<c:out value="${community.name}" escapeXml="true" />">
                                            <c:out value="${community.name}" escapeXml="true"/>
                                        </option>
                                    </c:forEach>
                                </form:select>
                                <form:errors path="community" cssStyle="color: red" cssClass="error"/>
                            </div>
                            <div>
                                <label for="bodyInput" class="form-label"><spring:message code="Post.Category"/></label>
                                <form:select name="category" path="category"
                                             class="form-select" id="specialtiesSelect">
                                    <option disabled selected hidden><spring:message
                                            code="Post.ChooseCategory"/></option>
                                    <c:forEach var="category" items="${categories}">--%>
                                        <option value="<c:out value="${category}" escapeXml="true" />">
                                            <c:out value="${category}" escapeXml="true"/>
                                        </option>
                                    </c:forEach>
                                </form:select>
                                <form:errors path="category" cssStyle="color: red" cssClass="error"/>
                            </div>
                            <div class="item-upload">
                                <spring:message code="Post.Image"/>
                                <div class="input-group mb-3 mt-2">
                                    <label class="input-group-text" for="files"><i class="fa-solid fa-file"></i></label>
                                    <form:input type="file" accept="image/*" class="form-control" name="files" path="files"
                                                multiple="true"/>
                                </div>
                                <div id="photo-upload__preview" class="upload-preview"></div>
                                <form:errors path="files" cssStyle="color: red"/>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary"><spring:message
                                code="Post.CreateButton"/></button>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                    </form:form>
                </div>
            </div>
        </div>

        <%--POSTS LIST OF THE COMMUNITY--%>
        <div class="col-2">
            <div class="card  border-0 bg-transparent">
                <div class="card-body">
                    <c:forEach items="${news}" var="otherPost">
                    <c:url value="/post/${otherPost.id}" var="postUrl"/>
                    <a href="${postUrl}" class="text-decoration-none text-body-primary">
                        <div class="card mb-3">
                            <div class="card-body">
                                <c:if test="${!otherPost.deleted}">
                                    <h5 class="card-title card-title other-post-title fw-bold mb-1"><c:out
                                            value="${otherPost.title}"
                                            escapeXml="true"/>
                                    </h5>
                                    <span class="badge rounded-pill ${otherPost.category} mb-1">${otherPost.category}</span>
                                    <p class="card-text post-body"><c:out value="${otherPost.body}"
                                                                          escapeXml="true"/></p>
                                </c:if>
                                <c:if test="${otherPost.deleted}">
                                    <h5 class="card-title
                                 card-title other-post-title fw-bold mb-1"><spring:message code="Post.Deleted"/>
                                    </h5>
                                    <span class="badge rounded-pill ${otherPost.category} mb-1">${otherPost.category}</span>
                                    <p class="card-text post-body"><spring:message code="Post.Deleted"/></p>
                                </c:if>
                            </div>
                        </div>
                        </c:forEach>
                    </a>
                </div>
            </div>
        </div>

    </div>
</div>
</body>
</html>
<script lang="javascript">
    // $('.dropdown-toggle').dropdown();

    function previewImage(e, selectedFiles, imagesArray) {
        const elemContainer = document.createElement('div');
        elemContainer.setAttribute('class', 'item-images d-flex flex-wrap justify-content-start align-items-center');
        for (let i = 0; i < selectedFiles.length; i++) {
            imagesArray.push(selectedFiles[i]);
            const imageContainer = document.createElement('div');
            const elem = document.createElement('img');
            elem.setAttribute('src', URL.createObjectURL(selectedFiles[i]));
            elem.setAttribute('class', 'photo-upload__preview')
            elem.setAttribute('style', 'width: 100px; height: 100px; object-fit: cover; margin-top:10px;')
            const removeButton = document.createElement('button');
            removeButton.setAttribute('type', 'button');
            removeButton.setAttribute('class', 'btn-close delete');
            removeButton.classList.add('delete');
            removeButton.dataset.filename = selectedFiles[i].name;
                // removeButton.innerHTML = '<span>&times;</span>'
                imageContainer.appendChild(elem);
            imageContainer.appendChild(removeButton);
            elemContainer.appendChild(imageContainer);
        }
        return elemContainer;
    }

    let item_images = [];
    document.getElementById('files').addEventListener('change', (e) => {
        let selectedFiles = e.target.files;
        const photoPreviewContainer = document.querySelector('#photo-upload__preview');
        photoPreviewContainer.childNodes.forEach(child => child.remove());
        const elemContainer = previewImage(e, selectedFiles, item_images);
        photoPreviewContainer.appendChild(elemContainer);
    });

    document.getElementById('photo-upload__preview').addEventListener('click', (e) => {
        const tgt = e.target.closest('button');
        if (tgt.classList.contains('delete')) {
            tgt.closest('div').remove();
            const fileName = tgt.dataset.filename
            item_images = item_images.filter(img => img.name != fileName)
        }
    })
// mmmm no se si ese = esta bien, chequear si no va un = solo
</script>

