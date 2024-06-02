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
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
            integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/rateYo/2.3.2/jquery.rateyo.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/rateYo/2.3.2/jquery.rateyo.min.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>

<!-- Modal -->
<div class="modal fade" id="createPostModal" tabindex="-1" aria-labelledby="createPostModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <c:url var="postUrl" value="/community/${community.encodedName}"/>
            <form:form action="${postUrl}" method="post" modelAttribute="newPostForm" id="postForm"
                       enctype="multipart/form-data">
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
                            <form:errors path="body" cssStyle="color: red" cssClass="error"/>
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
                            <form:errors path="category" cssStyle="color: red" cssClass="error"/>
                            <form:hidden path="community" value="${community.name}"/>
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
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><spring:message
                            code="Close"/></button>
                    <button type="button" onclick="submit()" class="btn btn-primary" data-bs-dismiss="modal">
                        <spring:message code="Post.CreateButton"/></button>
                    <form:errors cssStyle="color: red" cssClass="error"/>
                </div>
            </form:form>
        </div>
    </div>
</div>
<!-- Rating Modal -->
<div class="modal fade" id="ratingModal" tabindex="-1" aria-labelledby="ratingModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <c:if test="${rating == null}">
                <c:url var="ratingUrl" value="/community/${community.encodedName}/rate"/>
                <form:form action="${ratingUrl}" method="post" modelAttribute="newRatingForm" id="ratingForm">
                    <div class="modal-header">
                        <h5 class="modal-title" id="ratingModalLabel"><spring:message code="RateCommunity"/></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="ratingInput" class="form-label"><spring:message code="Rating"/></label>
                            <form:input path="rating" type="number" min="1" max="5" step="0.1" class="form-control"
                                        id="ratingInput"/>
                            <form:errors path="rating" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <form:hidden path="communityId" value="${community.id}"/>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary"><spring:message code="RateSumbit"/></button>
                    </div>
                </form:form>
            </c:if>
            <c:if test="${rating != null}">
                <div class="modal-header">
                    <h5 class="modal-title" id="ratingModalLabel"><spring:message code="RateCommunity"/></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="ratingInput" class="form-label"><spring:message code="YourRateCommunity"/></label>
                        <div id="rateYoSelf"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </c:if>
        </div>
    </div>
</div>

<div class=" container-fluid">
    <div class="row  min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <div class="col-1">
        </div>
        <div class="col-6 mt-5">
            <div class="card border-0">
                <div class="card-body">
                    <div class="row w-100 mb-2">
                        <div class="col-4">
                            <c:if test="${ empty community.portrait}">
                                <img src="${pageContext.request.contextPath}/images/default-community.png"
                                     class="w-100 rounded-1 img-thumbnail img-com" alt="Profile Picture">
                            </c:if>
                            <c:if test="${not empty community.portrait}">
                                <img src="<c:url value='/image/${community.portrait.imageId}'/>"
                                     class="w-100 rounded-1 img-thumbnail img-com" alt="Profile Picture">
                            </c:if>

                        </div>
                        <div class="col-8">
                            <div class="d-flex row-cols-2 justify-content-between">
                                <h1 class="card-title fw-bold"><c:out value="c/${community.name}"
                                                                      escapeXml="true"/></h1>
                                <div class="d-none">
                                    <c:url var="followUrl" value="/community/${community.encodedName}/follow"/>
                                    <form:form modelAttribute="followCommunityForm" action="${followUrl}" method="post"
                                               id="followForm">
                                        <form:hidden path="communityName" value="${community.name}"/>
                                        <form:hidden path="communityId" value="${community.id}"/>
                                    </form:form>
                                </div>
                                <c:if test="${isFollowing}">
                                    <button onClick="follow()"
                                            class="rounded-pill  btn-outline-info follow-button fw-bold"
                                            id="followButton"><spring:message code="Following"/>
                                    </button>
                                </c:if>
                                <c:if test="${!isFollowing}">
                                    <button onClick="follow()"
                                            class="rounded-pill   btn-outline-info  follow-button fw-bold"
                                            id="followButton"><spring:message code="Follow"/>
                                    </button>
                                </c:if>
                            </div>

                            <c:forEach var="category" items="${community.category}">
                                <c:url value="/communities?searchTerms=&categories=${category.toString()}"
                                       var="categorySearchUrl"/>
                                <a class="text-decoration-none" href="${categorySearchUrl}">
                                    <span class="fs-6 pe-auto btn btn-secondary cat-badge p-1 badge">${category.toString()}</span>
                                </a>
                            </c:forEach>
                            <div>
                                <h6 class="fw-bold"><spring:message code="Developer"/>: <c:out
                                        value="${community.developer}" escapeXml="true"/></h6>
                                <h6 class="fw-bold"><spring:message code="Publisher"/>: <c:out
                                        value="${community.publisher}" escapeXml="true"/></h6>
                                <%--                                        <h6 class="fw-bold">Release Date: ${community.releaseDate}</h6>--%>
                                <div class="d-flex flex-row w-100">
                                    <c:if test="${community.ratingCount == 0}">
                                        <h6 class="fw-bold"><spring:message code="Rating"/>: <spring:message
                                                code="NoRating"/></h6>
                                    </c:if>
                                    <c:if test="${community.ratingCount != 0}">

                                        <h6 class="fw-bold"><spring:message code="Rating"/>:
                                            <div id="rateYo"></div>
                                            (<c:out value="${community.ratingCount}" escapeXml="true"/>)
                                        </h6>
                                    </c:if>
                                </div>


                            </div>
                            <h5 class="card-subtitle text-secondary mt-3 mb-1"><c:out value="${community.description}"
                                                                                      escapeXml="true"/></h5>
                        </div>
                    </div>
                    <div class="d-flex <c:if test="${canEdit}"> justify-content-between </c:if> <c:if test="${!canEdit}"> justify-content-end </c:if>  mb-3 ">
                        <c:if test="${canEdit}">
                            <a href="<c:url value="/community/${communityName}/info"/>">
                                <button type="button" class="btn btn-primary ">
                                    <spring:message code="Community.Edit"/>
                                </button>
                            </a>
                        </c:if>
                        <div>
                            <button type="button" class="btn btn-primary round-btn" data-bs-toggle="modal"
                                    data-bs-target="#ratingModal">
                                <i class="fa fa-star"></i>
                            </button>
                            <button type="button" class="btn btn-primary round-btn" data-bs-toggle="modal"
                                    data-bs-target="#createPostModal">
                                <i class="fa-solid fa-plus"></i>
                            </button>
                        </div>
                    </div>
                    <c:if test="${empty posts.data}">
                        <h3 class="text-center mt-5"><spring:message code="Community.NoPost"/></h3>
                    </c:if>
                    <c:forEach var="post" items="${posts.data}">
                        <c:url value="/post/${post.id}" var="postUrl"/>
                        <a href="${postUrl}" class="card-link text-decoration-none">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="title-container">
                                            <%--                                        <c:if test="${community.portrait_id == 0}">--%>
                                            <%--                                            <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"--%>
                                            <%--                                                 class="very-small-profile-pic mb-1" alt="Profile Picture">--%>
                                            <%--                                        </c:if>--%>
                                            <%--                                        <c:if test="${community.portrait_id != 0}">--%>
                                            <%--                                            <img src="<c:url value='/image/${community.portrait_id}'/>"--%>
                                            <%--                                                 class="very-small-profile-pic mb-1" alt="Profile Picture">--%>
                                            <%--                                        </c:if>                                            --%>
                                        <p class="fw-semibold card-subtitle">/<c:out value="${post.communityName}"
                                                                                     escapeXml="true"/></p>
                                        <span class="badge rounded-pill mb-1 ${post.category}">${post.category}</span>
                                    </div>
                                    <c:if test="${!post.deleted}">
                                        <div>
                                            <h4 class="card-title fw-bold"><c:out value="${post.title}"
                                                                                  escapeXml="true"/></h4>
                                            <p class="card-text post-body"><c:out value="${post.body}"
                                                                                  escapeXml="true"/></p>
                                        </div>
                                        <div>

                                        </div>
                                    </c:if>
                                    <div class="d-flex row-cols-2 justify-content-between mt-1">
                                        <p>
                                            <small class="text-body-secondary">
                                                <c:out value="${post.date.format(format)}" escapeXml="true"/>
                                            </small>
                                        </p>
                                        <span class="badge rounded-pill text-bg-primary groovy-pill border-1"><c:out
                                                value="${post.grooviness}" escapeXml="true"/></span>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                    <div class="d-flex justify-content-center align-items-center">
                        <c:set var="paginatedDataWrapper" value="${posts}" scope="request"/>
                        <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                        <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                    </div>
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
    if (document.getElementsByClassName("error").length > 0) {
        var myModal = new bootstrap.Modal(document.getElementById('createPostModal'))
        myModal.show()
    }
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
        elemContainer.setAttribute('class', 'item-images d-flex flex-row flex-wrap justify-content-start align-items-center');
        for (let i = 0; i < selectedFiles.length; i++) {
            imagesArray.push(selectedFiles[i]);
            const imageContainer = document.createElement('div');
            const elem = document.createElement('img');
            elem.setAttribute('src', URL.createObjectURL(selectedFiles[i]));
            elem.setAttribute('class', 'photo-upload__preview')
            elem.setAttribute('style', 'width: 100px; height: 100px; object-fit: cover; margin-top: 5px;')
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
        photoPreviewContainer.setAttribute("class", "d-flex flex-row")
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

    $(document).ready(function () {
        $("#rateYo").rateYo({
            numStars: 5,
            halfStar: true,
            precision: 2,
            rating: ${community.totalRating/community.ratingCount},
            readOnly: true,
            starWidth: "20px"
        });
    });

    $(document).ready(function () {
        $("#rateYoSelf").rateYo({
            numStars: 5,
            halfStar: true,
            precision: 2,
            rating: ${rating.rating},
            readOnly: true,
            starWidth: "20px"
        });
    });
</script>