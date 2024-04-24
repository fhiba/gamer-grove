<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>/${community.name}</title>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://kit.fontawesome.com/002da5939d.js" crossorigin="anonymous"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>

<!-- Modal -->
<div class="modal fade" id="createPostModal" tabindex="-1" aria-labelledby="createPostModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <c:url var="postUrl" value="/community/${community.name}"/>
            <form:form action="${postUrl}" method="post" modelAttribute="newPostForm" id="postForm" enctype="multipart/form-data">
                <div class="modal-header">
                    <h5 class="modal-title" id="createPostModalLabel"><spring:message code="Post.Create"/></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="mb-3"><label for="titleInput" class="form-label"><spring:message
                                code="Post.Title"/></label>
                            <form:input path="title" class="form-control" id="titleInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="bodyInput" class="form-label"><spring:message code="Post.Body"/></label>
                            <form:textarea path="body" class="form-control" id="bodyInput"/>
                            <form:errors path="title" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mb-3">
                            <label for="bodyInput" class="form-label"><spring:message code="Post.Category"/></label>
                            <form:select itemValue="${categories}" name="category" path="category"
                                         class="form-select" id="specialtiesSelect">
                                <option disabled selected hidden><spring:message code="Post.ChooseCategory"/></option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="<c:out value="${category}" escapeXml="true" />">
                                        <c:out value="${category}" escapeXml="true"/>
                                    </option>
                                </c:forEach>
                            </form:select>
                            <form:hidden path="community" value="${community.name}"/>
                        </div>
                        <div class="item-upload">
                            <spring:message code="Post.Image"/>
                            <div class="input-group mb-3 mt-2">
                                <label class="input-group-text" for="files"><i class="fa-solid fa-file"></i></label>
                                <form:input type="file" class="form-control" name="files" path="files" multiple="true"/>
                            </div>
                            <div id="photo-upload__preview" class="upload-preview"></div>
                            <form:errors path="files" cssStyle="color: red"/>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" onclick="submit()" class="btn btn-primary" data-bs-dismiss="modal">
                        <spring:message code="Post.CreateButton"/></button>
                    <form:errors cssStyle="color: red" cssClass="error"/>
                </div>
            </form:form>
        </div>
    </div>
</div>
<div class=" container-fluid">
    <div class="row  min-vh-100">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card m-auto">
                <div class="card-body">
                    <c:url value="/home" var="homeUrl"/>
                    <a href="${homeUrl}" class="text-decoration-none card-title text-light mb-3">
                        <h5><spring:message code="Navbar.Home"/></h5>
                    </a>
                    <c:url value="/all" var="allUrl"/>
                    <a href="${allUrl}" class="text-decoration-none card-title text-light mb-3">
                        <h5><spring:message code="All"/></h5>
                    </a>
                    <c:if test="${isLogged == null}">
                        <div class="h5 card-title text-light mb-3">Communities</div>
                    </c:if>
                    <c:if test="${isLogged != null}">
                        <div class="card-title text-light mb-3">My Communities</div>
                    </c:if>
                    <c:forEach var="community" items="${communities}">
                        <c:url value="/community/${community.name}" var="communityUrl"/>
                        <a href="${communityUrl}" class="text-light text-decoration-none">
                            <div class="card-body-community d-flex align-items-center text-decoration-none mb-3">
                                <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                     class="very-small-profile-pic mb-1" alt="Profile Picture">
                                <div class="text-decoration-none">
                                    <h5 class="fw-semibold card-subtitle ">
                                        /<c:out value="${community.name}" escapeXml="true"/>
                                    </h5>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-1">
        </div>
        <div class="col-6 mt-5">
            <div class="card border-0">
                <div class="card-body">
                    <div class="row w-100 mb-2">
                        <div class="col-4">
                            <c:if test="${community.portrait_id == 0}">
                                <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                     class="w-100 rounded-1 img-thumbnail " alt="Profile Picture">
                            </c:if>
                            <c:if test="${community.portrait_id != 0}">
                                <img src="<c:url value='/image/${community.portrait_id}'/>"
                                     class="w-100 rounded-1 img-thumbnail" alt="Profile Picture">
                            </c:if>

                        </div>
                        <div class="col-8">
                            <div class="d-flex row-cols-2 justify-content-between">
                                <h1 class="card-title fw-bold"><c:out value="c/${community.name}"
                                                                      escapeXml="true"/></h1>
                                <div class="d-none">
                                    <c:url var="followUrl" value="/community/${community.name}/follow"/>
                                    <form:form modelAttribute="followCommunityForm" action="${followUrl}" method="post"
                                               id="followForm">
                                        <form:hidden path="communityName" value="${community.name}"/>
                                        <form:hidden path="communityId" value="${community.id}"/>
                                    </form:form>
                                </div>
                                <c:if test="${isFollowing}">
                                    <button onClick="follow()"
                                            class="rounded-pill  btn-outline-danger follow-button fw-bold"
                                            id="followButton">Following
                                    </button>
                                </c:if>
                                <c:if test="${!isFollowing}">
                                    <button onClick="follow()"
                                            class="rounded-pill   btn-outline-danger  follow-button fw-bold"
                                            id="followButton">Follow
                                    </button>
                                </c:if>
                            </div>
                            <c:forEach var="category" items="${community.categories}">
                                <span class="fs-6 cat-badge p-1 badge">${category}</span>
                            </c:forEach>
                            <div class="accordion accordion-flush justify-content-end">

                                    <h2 class="accordion-header justify-content-end">
                                        <button class="expand-btn" type="button"
                                                data-bs-toggle="collapse" data-bs-target="#flush-collapseOne"
                                                aria-expanded="false" aria-controls="flush-collapseOne"><i class="fa-solid fa-caret-down expand-btn"></i>
                                        </button>
                                    </h2>
                                    <div id="flush-collapseOne" class="accordion-collapse collapse"
                                         data-bs-parent="#accordionFlushExample">
                                        <h6 class="fw-bold">Developer: ${community.developer}</h6>
                                        <h6 class="fw-bold">Publisher: ${community.publisher}</h6>
<%--                                        <h6 class="fw-bold">Release Date: ${community.releaseDate}</h6>--%>

                                    </div>

                            </div>
                            <h5 class="card-subtitle text-secondary mt-3 mb-1"><c:out value="${community.description}"
                                                                                      escapeXml="true"/></h5>
                        </div>
                    </div>
                    <div class="d-flex <c:if test="${canEdit}"> justify-content-between </c:if> <c:if test="${!canEdit}"> justify-content-end </c:if>  mb-3 ">
                        <c:if test="${canEdit}">
                            <a href="<c:url value="/community/${communityName}/image"/>">
                                <button type="button" class="btn btn-primary ">
                                    <spring:message code="Community.Edit"/>
                                </button>
                            </a>
                        </c:if>
                        <button type="button" class="btn btn-primary round-btn" data-bs-toggle="modal"
                                data-bs-target="#createPostModal">
                            <i class="fa-solid fa-plus"></i>
                        </button>
                    </div>

                    <c:forEach var="post" items="${posts}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link text-decoration-none">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <p class="fw-semibold card-subtitle mb-1">
                                        <c:out value="c/${post.communityName}" escapeXml="true"/>
                                        <span class="badge rounded-pill ${post.category}">${post.category}</span>
                                    </p>
                                    <c:if test="${!post.deleted}">
                                        <h4 class="card-title fw-bold"><c:out value="${post.title}"
                                                                              escapeXml="true"/></h4>
                                        <p class="card-text post-body"><c:out value="${post.body}"
                                                                              escapeXml="true"/></p>
                                    </c:if>
                                    <c:if test="${post.deleted}">
                                        <h4 class="card-title fw-bold"><spring:message code="Post.Deleted"/></h4>
                                        <p class="card-text post-body"><<spring:message code="Post.Deleted"/>/></p>
                                    </c:if>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-3">
        </div>
    </div>
</div>
</body>
</html>
<script>
    let postBody = document.getElementsByClassName('post-body');
    console.log(postBody.length);
    for (let i = 0; i < postBody.length; i++) {
        if (postBody[i].innerText.length > 100) {
            postBody[i].innerText = postBody[i].innerText.substring(0, 100) + '...';
        }
    }

    let submit = () => {
        document.getElementById('postForm').submit();
    }

    function previewImage(e, selectedFiles, imagesArray) {
        const elemContainer = document.createElement('div');
        elemContainer.setAttribute('class', 'item-images');
        for (let i = 0; i < selectedFiles.length; i++) {
            imagesArray.push(selectedFiles[i]);
            const imageContainer = document.createElement('div');
            const elem = document.createElement('img');
            elem.setAttribute('src', URL.createObjectURL(selectedFiles[i]));
            elem.setAttribute('class', 'photo-upload__preview')
            elem.setAttribute('style', 'width: 100px; height: 100px; object-fit: cover;')
            const removeButton = document.createElement('button');
            removeButton.setAttribute('type', 'button');
            removeButton.setAttribute('class', 'btn-close delete');
            removeButton.classList.add('delete');
            removeButton.dataset.filename = selectedFiles[i].name,
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

    let follow = () => {
        document.getElementById('followForm').submit();
    }
</script>