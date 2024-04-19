<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title><spring:message code="Communities.Title"/> </title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
</head>
<body>
<c:set var="searchTerms" value="${searchTerms}" scope="request"/>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>

<div class="container-fluid">
    <div class="row mt-4 mb-3">
        <%--COMMUNITY LIST--%>
        <div class="col-3">


        </div>

            <%--LISTA DE COMMUNITIES--%>
            <div class="col-6">
                <div class="card  border-light">
                    <div class="card-body">
                        <c:if test="${empty communities}">
                            <div><spring:message code="Communities.NoCommunitites"/></div>
                        </c:if>
                        <c:forEach var="community" items="${communities}">
                            <c:url value="/community/${community.name}" var="communityUrl"/>
                            <a href="${communityUrl}" class="card-link link-underline-light">
                                <div class="card mb-3">
                                    <div class="card-body d-flex flex-row">
                                        <div class="flex-column">
                                            <img src="${pageContext.request.contextPath}/images/profile-picture.jpg" class="medium-profile-pic" alt="Profile Picture">
                                        </div>
                                        <div class="flex-column overflow-auto w-100">
                                            <div class="d-flex justify-content-between">
                                                <h2 class="fw-semibold card-subtitle mb-1">
                                                    /${community.name}
                                                </h2>
                                                <div class="justify-content-end">
                                                <c:forEach var="communityCategories" items="${community.categories}">
                                                    <span class="fs-6 cat-badge p-1 badge bg-dark">${communityCategories}</span>
                                                </c:forEach>
                                                </div>
                                            </div>
                                            <h6 class="card-title text-secondary">${community.description}</h6>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>
        <%--LISTA DE Filters--%>
        <div class="col-3">
            <div class="card  border-light">
                <div class="card-body">
                    <h5><spring:message code="Home.FilterCategory"/> </h5>
                    <div id="categoryPills" class="d-flex flex-row flex-wrap">
                    </div>
                    <div class="d-flex border border-dark-subtle">
                        <div class="accordion accordion-flush w-100" id="accordionFlushExample">
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="flush-headingOne">
                                    <button id="addCategoryButton"  class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseOne" aria-expanded="false" aria-controls="flush-collapseOne">
                                        Add Category
                                    </button>
                                </h2>
                                <div id="flush-collapseOne" class="accordion-collapse collapse border border-top-dark" aria-labelledby="flush-headingOne" data-bs-parent="#accordionFlushExample">
                                    <div id="categoriesBody" class="accordion-body"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
</div>

</body>
</html>
<script>
    const buttonToShowString = "btn btn-outline-dark";
    const buttonToHideString = "btn btn-outline-danger";
    let applyFilterArray = [];
    let selectArray = [];
    let addCategoryToBody = (category) => {
        let body = document.getElementById("categoriesBody");
        let button = document.createElement("button");
        button.id = category+'Option';
        button.innerHTML = category;
        button.setAttribute("class", "btn btn-outline-dark m-1");
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


    let showSelect = () => {
        let selectDiv = document.getElementById("selectDiv");

        let addCategoryButton = document.getElementById("addCategoryButton");
        addCategoryButton.setAttribute("class", selectDiv.hidden ? buttonToHideString : buttonToShowString);
        addCategoryButton.innerHTML = selectDiv.hidden ? "cancel" : "add category";
        selectDiv.hidden = !selectDiv.hidden;
    };


    let createPill = (selected) => {
        let categoryPills = document.getElementById("categoryPills");
        let pill = document.createElement("div");
        pill.id = selected+'Pill';
        pill.setAttribute("class", "card flex-row align-items-center justify-content-center m-1");
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
        removeButton.setAttribute("class", "btn-close flex-col");
        removeButton.onclick = () => {
            removeCategory(selected);
        };
        innerDiv.appendChild(removeButton);

    }

    let initializeArray = () => {
        <c:forEach var="category" items="${selectedCategories}">
        if("${category}" !== null && "${category}" !== "") {
            applyFilterArray.push("${category}");
            createPill("${category}");
        }
        </c:forEach>
        <c:forEach var="category" items="${categories}">
        if(!applyFilterArray.includes("${category}")){
            selectArray.push("${category}");
            addCategoryToBody("${category}");
        }
        </c:forEach>
    }
    initializeArray();

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
</script>
