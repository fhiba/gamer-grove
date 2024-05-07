<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="Communities.Create"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <div class="col-2 sidebar">
            <div class="card sidebar-card m-auto">
                <div class="card-body">
                    <c:if test="${isAdmin}">
                        <div class="row-cols-2">
                            <c:url value="/addMod" var="addModUrl"/>
                            <a href="${addModUrl}">
                                <button class="btn btn-outline-primary">Add Mod</button>
                            </a>
                            <c:url value="/new-community" var="newCommunityUrl"/>
                            <a href="${newCommunityUrl}">
                                <button class="ms-2 btn btn-outline-success">Add Community</button>
                            </a>
                        </div>
                    </c:if>
                    <hr>
                    <c:url value="/home" var="homeUrl"/>
                    <a href="${homeUrl}" class="text-decoration-none card-title text-light mb-3">
                        <h5><spring:message code="Navbar.Home"/></h5>
                    </a>
                    <hr>

                    <c:url value="/all" var="allUrl"/>
                    <a href="${allUrl}" class="text-decoration-none card-title text-light mb-3">
                        <h5><spring:message code="All"/></h5>
                    </a>
                    <hr>
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
                                <c:if test="${community.portrait_id == 0}">
                                    <img src="${pageContext.request.contextPath}/images/profile-picture.jpg"
                                         class="very-small-profile-pic mb-1" alt="Profile Picture">
                                </c:if>
                                <c:if test="${community.portrait_id != 0}">
                                    <img src="<c:url value='/image/${community.portrait_id}'/>"
                                         class="very-small-profile-pic mb-1" alt="Profile Picture">
                                </c:if>
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
        <%--NEW COMMUNITY FORM--%>
        <div class="col-10 d-flex justify-content-center">
            <div class="card border-0 w-100 ms-5">
                <div class="card-body">
                    <div class="mb-3 row w-100">
                        <div class="col-3">
                            <c:url var="communityUrl" value="/new-community"/>
                            <form:form action="${communityUrl}" method="post" modelAttribute="newCommunityForm"
                                       id="myForm"
                                       onsubmit="addCategoriesToForm()">
                                <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                     class="w-100 h-75 rounded-1" id="imgFile" alt="Profile Picture">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Profile.UpdateProfilePicture"/></label>
                            <form:input
                                    onchange="document.getElementById('imgFile').src = window.URL.createObjectURL(this.files[0])"
                                    path="image" class="form-control w-100" type="file"/>
                            <p class="mt-3 mb-auto"><form:errors path="image" cssStyle="color: red"
                                                                 cssClass="error"/></p>
                            <form:errors cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="col-6">
                            <h3 class="fw-bold"><spring:message code="NewCommunity"/> </h3>
                            <div class="mt-2">
                                <label class="form-label fw-semibold"><spring:message
                                        code="Name"/>:</label>
                                <form:input path="name"/>
                                <form:errors path="name" cssStyle="color: red" cssClass="error"/>
                            </div>
                            <div class="mt-2">
                                <label class="form-label fw-semibold"><spring:message
                                        code="Description"/>:</label>
                                <form:input path="description"/>
                                <form:errors path="description" cssStyle="color: red" cssClass="error"/>
                            </div>
                            <div class="mt-2">
                                <label class="form-label fw-semibold"><spring:message
                                        code="Developer"/>:</label>
                                <form:input path="developer"/>
                                <form:errors path="developer" cssStyle="color: red" cssClass="error"/>
                            </div>
                            <div class="mt-2">
                                <label class="form-label fw-semibold"><spring:message
                                        code="Publisher"/>:</label>
                                <form:input path="publisher"/>
                                <form:errors path="publisher" cssStyle="color: red" cssClass="error"/>
                            </div>
                            <div id="categoryPills" class="d-flex flex-row">
                            </div>
                            <div class="d-flex mt-5">
                                <div id="selectDiv">
                                    <label for="select"></label>
                                </div>
                            </div>
                            <form:errors cssStyle="color: red" cssClass="error"/>
                            <form:errors cssStyle="color: red" cssClass="error" path="categories"/>
                        </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<script language="JavaScript">
    let hiddenArray = [];
    let selectArray = [];
    let addOptionToSelect = (category) => {
        let select = document.getElementById("select");
        let option = document.createElement("option");
        option.id = category + 'Option';
        option.text = category;
        option.value = category;
        select.add(option);
    };
    let addCategory = () => {
        let select = document.getElementById("select");
        let selected = select.options[select.selectedIndex].value;

        //update arrays
        hiddenArray.push(selected);
        selectArray = selectArray.filter(category => category !== selected);

        //remove selected option from select
        let selectedOption = document.getElementById(selected + 'Option');
        selectedOption.remove();

        //create pill
        createPill(selected);
        select.remove();
        createSelect();

        //hide selectDiv
        let selectDiv = document.getElementById("selectDiv");
    }

    createSelect = () => {
        console.log("create select")
        let select = document.getElementById("select");
        if (select) {
            console.log("select removed")
            select.remove();
        }
        select = document.createElement("select");
        select.id = "select";
        select.onchange = addCategory;
        select.setAttribute("class","form-select");
        document.getElementById("selectDiv").appendChild(select);
        let defaultOption = document.createElement("option");
        defaultOption.hidden = true;
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.text = "Categories";
        select.appendChild(defaultOption);
        selectArray.sort((a, b) => a.localeCompare(b));
        for (category of selectArray) {
            addOptionToSelect(category);
        }
    }

    let initializeArray = () => {
        <c:forEach var="category" items="${categories}">
        selectArray.push("${category}");
        </c:forEach>
        //sort categories alphabetically
        createSelect();
    }
    initializeArray();


    let createPill = (selected) => {
        let categoryPills = document.getElementById("categoryPills");
        let pill = document.createElement("div");
        pill.id = selected + 'Pill';
        pill.setAttribute("class", "card border-light flex-row align-items-center justify-content-center m-2");
        let innerDiv = document.createElement("div");
        innerDiv.setAttribute("class", "card-body border-light d-flex flex-row p-2 align-items-center justify-content-center");
        pill.appendChild(innerDiv);
        let p = document.createElement("p");
        p.setAttribute("class", "m-0 me-1");
        p.innerHTML = selected;
        innerDiv.appendChild(p);
        categoryPills.appendChild(pill);
        let removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.setAttribute("class", "btn-close-white flex-col");
        removeButton.onclick = () => {
            removeCategory(selected);
        };
        innerDiv.appendChild(removeButton);
    }


    let removeCategory = (category) => {
        //update arrays
        selectArray.push(category);
        hiddenArray = hiddenArray.filter(cat => cat !== category);
        //re-sort categories and reinsert them on select
        createSelect();
        //remove pill

        let pill = document.getElementById(category + 'Pill');
        pill.remove();
    }

    let addCategoriesToForm = () => {
        let form = document.getElementById("myForm");
        hiddenArray.forEach((category) => {
            let input = document.createElement("input");
            input.type = "hidden";
            input.setAttribute("id", "categories");
            input.name = "categories";
            input.value = category;
            form.appendChild(input);
        });

    }
</script>
