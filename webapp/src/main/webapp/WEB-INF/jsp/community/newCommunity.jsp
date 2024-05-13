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
    <%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://kit.fontawesome.com/002da5939d.js" crossorigin="anonymous"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
        <c:set var="isLogged" value="${isLogged}" scope="request"/>
        <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>
        <%--NEW COMMUNITY FORM--%>
        <div class="col-2"></div>
        <div class="col-8 d-flex justify-content-start mt-5">
            <div class="card border-0 w-50 ms-5">
                <div class="card-body">
                    <h3 class="fw-bold"><spring:message code="NewCommunity"/></h3>
                    <c:url var="communityUrl" value="/new-community"/>
                    <form:form action="${communityUrl}" method="post" modelAttribute="newCommunityForm"
                               id="myForm"
                               onsubmit="addCategoriesToForm()" enctype="multipart/form-data">

                        <div class="mt-2 d-flex flex-column">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Name"/>:</label>
                            <form:input path="name"/>
                            <form:errors path="name" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mt-2 d-flex flex-column">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Description"/>:</label>
                            <form:textarea path="description"/>
                            <form:errors path="description" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mt-2 d-flex flex-column">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Developer"/>:</label>
                            <form:input path="developer"/>
                            <form:errors path="developer" cssStyle="color: red" cssClass="error"/>
                        </div>
                        <div class="mt-2 d-flex flex-column">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Publisher"/>:</label>
                            <form:input path="publisher"/>
                            <form:errors path="publisher" cssStyle="color: red" cssClass="error"/>
                        </div>


                        <div id="categoryPills" class="d-flex flex-row">
                        </div>
                        <div class="d-flex mt-2">
                            <div id="selectDiv">
                                <label for="select"></label>
                            </div>
                        </div>
                        <div class="mt-2 d-flex flex-column">
                            <label class="form-label fw-semibold"><spring:message
                                    code="Profile.AddCommunityProfilePictire"/></label>
                            <form:input
                                    id="image-input"
                                    path="image" class="form-control w-100" type="file" accept="image/*"/>
                            <p class="mt-3 mb-auto"><form:errors path="image" cssStyle="color: red"
                                                                 cssClass="error"/></p>
                            <form:errors cssStyle="color: red" cssClass="error"/>
                            <img src="${pageContext.request.contextPath}/images/default-avatar-icon.jpg"
                                 class="w-25 h-25 rounded-1"  id="preview-image" alt="Profile Picture">
                        </div>
                        <form:errors cssStyle="color: red" cssClass="error"/>
                        <form:errors cssStyle="color: red" cssClass="error" path="categories"/>
                        <button class="btn btn-outline-primary w-25 mt-3" type="submit"><spring:message
                                code="Post.CreateButton"/></button>
                    </form:form>
                </div>
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
        select.setAttribute("class", "form-select");
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
        removeButton.setAttribute("class", "btn-close btn-close-white flex-col");
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

    const imageInput = document.getElementById('image-input');
    const previewImage = document.getElementById('preview-image');
    previewImage.style.display = 'none';
    imageInput.addEventListener('change', function(event) {
        console.log(previewImage.style.display)
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
            }
            reader.readAsDataURL(file);
        } else {
            previewImage.src = '#';
            previewImage.style.display = 'none';
        }
    });
</script>
