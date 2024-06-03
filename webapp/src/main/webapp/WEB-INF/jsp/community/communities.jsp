<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="Communities.Title"/></title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://kit.fontawesome.com/002da5939d.js" crossorigin="anonymous"></script>
</head>
<body>
<c:set var="searchTerms" value="${searchTerms}" scope="request"/>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>


<!-- Modal -->
<div class="modal fade" id="onboardingModalVerified" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
     aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <c:if test="${!isVerified}">
                <c:url value="auth/resend-verification" var="verifyUrl"/>
                <spring:message code="VerifyAccount.Verify"/>
                <a href="${verifyUrl}">
                    <button type="button" class="btn btn-primary btn-sm"><spring:message
                            code="VerifyAccount.Resend"/></button>
                </a>
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">
                    <spring:message
                            code="Close"/></button>
            </c:if>
            <c:if test="${isVerified}">
                <div class="modal-header">
                    <h5 class="modal-title" id="staticBackdropLabel"><spring:message code="VerifyAccount.Success"/></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div>
                        <p><spring:message code="WelcomeMessage"/></p>
                    </div>
                    <div id="onboardingPills" class="d-flex flex-row flex-wrap mb-3"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="addCategoriesToForm()">Done</button>
                </div>
            </c:if>
        </div>
    </div>
</div>

<div class="modal fade" id="unverifiedModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
     aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    You need to verify your account!
                </h5>
            </div>
            <div class="modal-body">
                <p>Check your email and verify your account before continuing</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Done</button>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${followedCommunities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <div class="col-1">
        </div>
        <%--LISTA DE COMMUNITIES--%>
        <div class="col-5">

            <div class="card border-0 text-decoration-none">
                <div class="card-body">
                    <c:if test="${empty communitiesPaginated.data}">
                        <div class="d-flex flex-column align-items-center">
                            <h4 class="fw-semi-bold"><spring:message code="Communities.NoCommunities"/></h4>
                            <c:url value="/communities" var="showAll"/>
                            <a href="${showAll}" class="btn btn-primary"><spring:message
                                    code="Communities.searchAll"/></a>
                        </div>
                    </c:if>
                    <c:if test="${not empty communitiesPaginated}">
                        <c:forEach var="community" items="${communitiesPaginated.data}">
                            <c:url value="/community/${community.encodedName}" var="communityUrl"/>
                            <a href="${communityUrl}" class="card-link text-decoration-none">
                                <div class="card mb-3">
                                    <div class="card-body d-flex flex-row">
                                        <div class="flex-column">
                                            <c:if test="${empty community.portrait}">
                                                <img src="${pageContext.request.contextPath}/images/default-community.png"
                                                     class="very-small-profile-pic mb-1" alt="Profile Picture">
                                            </c:if>
                                            <c:if test="${not empty community.portrait}">
                                                <img src="<c:url value='/image/${community.portrait.imageId}'/>"
                                                     class="very-small-profile-pic mb-1" alt="Profile Picture">
                                            </c:if>
                                        </div>
                                        <div class="flex-column overflow-auto w-100">
                                            <div class="d-flex justify-content-between">
                                                <h2 class="fw-semibold card-subtitle mb-1">
                                                    /<c:out value="${community.name}" escapeXml="true"/>
                                                </h2>
                                                <div class="justify-content-end">
                                                    <c:forEach var="communityCategories"
                                                               items="${community.category}">
                                                        <span class="fs-6 cat-badge p-1 badge bg-dark-subtle text-dark">${communityCategories.toString()}</span>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                            <h6 class="card-title text-secondary"><c:out
                                                    value="${community.description}" escapeXml="true"/></h6>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                        <div class="d-flex justify-content-center align-items-center">

                            <div class="d-flex justify-content-center align-items-center">
                                <c:set var="paginatedDataWrapper" value="${communitiesPaginated}" scope="request"/>
                                <c:set var="pageNumberName" value="pageNumber" scope="request"/>
                                <jsp:include page="/WEB-INF/jsp/components/paginationFooter.jsp"/>
                            </div>

                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <%--LISTA DE Filters--%>
        <div class="col-3">
            <div class="card border-0 text-decoration-none">
                <div class="card-body">
                    <h5><spring:message code="Home.FilterCategory"/></h5>
                    <div id="categoryPills" class="d-flex flex-row flex-wrap mb-3">
                    </div>
                    <div class="d-flex border border-dark-subtle">
                        <div class="accordion accordion-flush w-100" id="accordionFlushExample">
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="flush-headingOne">
                                    <button id="addCategoryButton"
                                            class="accordion-button btn-light collapsed bg-dark text-light"
                                            type="button"
                                            data-bs-toggle="collapse" data-bs-target="#flush-collapseOne"
                                            aria-expanded="false" aria-controls="flush-collapseOne">
                                        <spring:message code="Category.Add"/>
                                    </button>
                                </h2>
                                <div id="flush-collapseOne"
                                     class="accordion-collapse bg-dark collapse border border-top-light justify-content-evenly"
                                     aria-labelledby="flush-headingOne" data-bs-parent="#accordionFlushExample">
                                    <div id="categoriesBody" class="accordion-body">

                                        <p class="text-dark-emphasis m-1" hidden="hidden" id="emptyCatText">
                                            <spring:message code="Category.NoMore"/></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="toastBox" class=" position-fixed bottom-0 end-0 m-3" style="display: none"
                 data-bs-autohide="false">
                <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false">
                    <div class="toast-header">
                        <strong id="toast_header" class="me-auto"></strong>
                        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                    <div class="toast-body text-dark" id="toast_body">

                    </div>
                </div>
            </div>
            <c:if test="${isLogged && !isVerified }">
                <c:url value="auth/resend-verification" var="verifyUrl"/>
                <div class="toast show position-fixed bottom-0 end-0 m-3" role="alert" aria-live="assertive"
                     aria-atomic="true" id="verifyToastBox">
                    <div class="toast-body text-dark">
                        <spring:message code="VerifyAccount.Verify"/>
                        <div class="mt-2 pt-2 border-top">
                            <a href="${verifyUrl}">
                                <button type="button" class="btn btn-primary btn-sm"><spring:message
                                        code="VerifyAccount.Resend"/></button>
                            </a>
                            <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="toast">
                                <spring:message
                                        code="Close"/></button>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>

    </div>
</div>

</body>
</html>
<script>
    let hasToast = document.URL.includes("verifySuccess");
    let hasCategories = document.URL.includes("&categories=");
    console.log("has categories es " + hasCategories);
    let isVerified = ${isVerified};
    let noTerms = ${noTerms};
    if (isVerified && !hasCategories && noTerms) {
        var myModal1 = new bootstrap.Modal(document.getElementById('onboardingModalVerified'))
        myModal1.show()
    }
    let hasRegistered = document.URL.includes("registerSuccess");
    if (hasRegistered) {
        var myModal2 = new bootstrap.Modal(document.getElementById('unverifiedModal'))
        myModal2.show()
    }

    const buttonToShowString = "btn btn-outline-dark";
    const buttonToHideString = "btn btn-outline-danger";
    let applyFilterArray = [];
    let selectArray = [];
    let auxArray = [];
    let addCategoryToBody = (category) => {
        let body = document.getElementById("categoriesBody");
        let button = document.createElement("button");
        button.id = category + 'Option';
        button.innerHTML = category;
        button.setAttribute("class", "btn btn-outline-light m-1");
        button.onclick = () => {
            addCategory(category);
        };
        body.appendChild(button);
    };
    let addCategory = (category) => {
        //update arrays
        applyFilterArray.push(category);

        addCategoriesToForm();
    }

    let selectCategory = (category) => {
        if(applyFilterArray.length < 1) {
            let selectedPill = document.getElementById(category + 'Pill');
            auxArray.push(category);
            applyFilterArray.push(category);
            selectedPill.classList.remove("class", "border-light");
            selectedPill.classList.add("class", "border-primary");
        }
    }

    let deselectCategory = (category) => {
        let selectedPill = document.getElementById(category + 'Pill');
        auxArray = auxArray.filter(cat => cat !== category);
        applyFilterArray = applyFilterArray.filter(cat => cat !== category);
        selectedPill.classList.remove("class","border-primary");
        selectedPill.classList.add("class","border-light");
    }

    let createOnboardingPill = (selected) => {
        let onboardingPills = document.getElementById("onboardingPills");
        let pill = document.createElement("div");
        pill.id = selected + 'Pill';
        pill.setAttribute("class", "card flex-row align-items-center border-light justify-content-center m-1 btn p-0");
        let innerDiv = document.createElement("div");
        innerDiv.setAttribute("class", "card-body d-flex flex-row p-2 align-items-center justify-content-center");
        pill.appendChild(innerDiv);
        let p = document.createElement("p");
        p.setAttribute("class", "m-0 me-1");
        p.innerHTML = selected;
        innerDiv.appendChild(p);
        onboardingPills.appendChild(pill);
        pill.onclick = () => {
            if(auxArray.includes(selected)){
                deselectCategory(selected);
            }else{
                selectCategory(selected);
            }
        };
    }

    let createPill = (selected) => {
        let categoryPills = document.getElementById("categoryPills");
        let pill = document.createElement("div");
        pill.id = selected + 'Pill';
        pill.setAttribute("class", "card flex-row align-items-center border-light justify-content-center m-1 btn p-0");
        let innerDiv = document.createElement("div");
        innerDiv.setAttribute("class", "card-body d-flex flex-row p-2 align-items-center justify-content-center");
        pill.appendChild(innerDiv);
        let p = document.createElement("p");
        p.setAttribute("class", "m-0 me-1");
        p.innerHTML = selected;
        innerDiv.appendChild(p);
        categoryPills.appendChild(pill);
        let removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.setAttribute("class", "btn btn-close btn-close-white flex-col");
        pill.onclick = () => {
            removeCategory(selected);
        };
        innerDiv.appendChild(removeButton);

    }

    let initializeArray = () => {
        <c:forEach var="category" items="${selectedCategories}">
        if ("${category}" !== null && "${category}" !== "") {
            applyFilterArray.push("${category}");
            createPill("${category}");
        }
        </c:forEach>
        if(auxArray.length !== 0) {
            for (let i = 0; i < auxArray.length; i++) {
                createPill(auxArray[i]);
            }
        }
        <c:forEach var="category" items="${categories}">
        if (!applyFilterArray.includes("${category}")) {
            selectArray.push("${category}");
            addCategoryToBody("${category}");
        }
        </c:forEach>
        if (selectArray.length === 0) {
            document.getElementById("emptyCatText").hidden = false;
        }
    }

    let initializeModalArray = () =>{
        <c:forEach var="category" items="${categories}">
            createOnboardingPill("${category}");
        </c:forEach>
    }

    initializeArray();
    initializeModalArray();
    let removeCategory = (category) => {
        applyFilterArray = applyFilterArray.filter(cat => cat !== category);
        addCategoriesToForm();
    }

    let addCategoriesToForm = () => {
        let form = document.getElementById("searchForm");
        let input = document.createElement("input");
        input.type = "hidden";
        input.setAttribute("id", "categories");
        input.name = "categories";
        input.value = applyFilterArray.join(",");
        form.appendChild(input);

        document.getElementById("searchButton").click();
    }

    const successMessage = "<spring:message code="VerifyAccount.Success"/>";
    const errorMessage = " <spring:message code="VerifyAccount.Error"/>";
    if (hasToast) {
        let toastMessage = document.URL.split("verifySuccess=")[1];
        console.log(toastMessage);
        document.getElementById('toast_header').innerText = "<spring:message code="Toast.Title.Notification"/>";
        document.getElementById('toast_body').innerText = toastMessage === 'true' ? successMessage : errorMessage;
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
    if (hasRegistered) {
        document.getElementById('verifyToastBox').style.display = 'none';
        document.getElementById('toast_header').innerText = "<spring:message code="Toast.Title.Welcome"/>";
        document.getElementById('toast_body').innerText = "<spring:message code="Register.Success"/>";
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
    let resendVerification = document.URL.includes("resendVerification");

    if (resendVerification) {

        document.getElementById('verifyToastBox').style.display = 'none';
        document.getElementById('toast_header').innerText = "<spring:message code="Toast.Title.Notification"/>";
        document.getElementById('toast_body').innerText = "<spring:message code="VerifyAccount.EmailSent"/>";
        document.getElementById('toastBox').style.display = 'block';
        new bootstrap.Toast(document.querySelector('.toast')).show();
    }
</script>