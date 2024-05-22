<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Edit /${community.name}</title>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/general-styling.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <%--suppress JSUnresolvedLibraryURL --%>
    <script src="https://kit.fontawesome.com/002da5939d.js" crossorigin="anonymous"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<div class="container-fluid">
    <div class="row min-vh-100">
        <%--COMMUNITY LIST--%>
        <c:set var="isAdmin" value="${isAdmin}" scope="request"/>
            <c:set var="communities" value="${communities}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/sidebar.jsp"/>

        <div class="col-10 d-flex justify-content-center">
            <div class="card border-0 w-100 ms-5">
                <div class="card-body">
                    <div class="mb-3 row w-100">
                        <div class="col-3">
                            <c:url value="/community/${community.encodedName}/info" var="editCommunityUrl"/>
                            <form:form method="POST" action="${editCommunityUrl}" enctype="multipart/form-data"
                                       modelAttribute="EditCommunityForm" id="myForm" onsubmit="addCategoriesToForm()">
                            <c:if test="${empty community.portrait }">

                                <img src="${pageContext.request.contextPath}/images/default-community.png"
                                     class="w-100 h-100 rounded-1" id="imgFile" alt="Profile Picture">
                            </c:if>
                            <c:if test="${not empty community.portrait }">

                                <img src="<c:url value='/image/${community.portrait.imageId}'/>"
                                     class="w-100 h-100 rounded-1" id="imgFile" alt="Profile Picture">
                            </c:if>
                            <label class="form-label fw-semibold"><spring:message
                                    code="Profile.UpdateProfilePicture"/></label>
                            <form:input
                                    onchange="document.getElementById('imgFile').src = window.URL.createObjectURL(this.files[0])"
                                    path="image" class="form-control w-50" type="file" accept="image/*"/>
                            <p class="mt-3 mb-auto"><form:errors path="image" cssStyle="color: red"
                                                                 cssClass="error"/></p>
                            <form:errors cssStyle="color: red" cssClass="error"/>

                        </div>
                        <div class="col-8">
                            <div class=" d-flex flex-column">

                                <h1><c:out value="${community.name}" escapeXml="true"/></h1>
                                <div class="me-5">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Description"/></label>
                                    <form:textarea path="description" class="form-control"
                                                 id="descriptionTextArea"   type="textarea" />
                                    <form:errors path="description" cssStyle="color: red" cssClass="error"/>
                                </div>
                                <div class="me-5 mt-3">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Publisher"/></label>
                                    <form:input value="${community.publisher}" path="publisher" class="form-control"
                                                type="text"/>
                                    <form:errors path="publisher" cssStyle="color: red" cssClass="error"/>
                                </div>
                                <div class="me-5 mt-3">
                                    <label class="form-label fw-semibold"><spring:message
                                            code="Developer"/></label>
                                    <form:input value="${community.developer}" path="developer" class="form-control"
                                                type="text"/>
                                    <form:errors path="developer" cssStyle="color: red" cssClass="error"/>
                                </div>
                                <div class="mt-5">
                                    <div id="categoryPills" class="d-flex flex-row">
                                    </div>
                                    <div class="d-flex">
                                        <div id="selectDiv" class="dropdown">
                                            <label for="select"></label>
                                        </div>
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary mt-5 w-25"><spring:message
                                        code="Update"/></button>
                            </div>
                        </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>

</body>
<script language="JavaScript">
    let hiddenArray = [];
    let selectArray = [];
    let aux = [];
    document.getElementById('descriptionTextArea').value = '<c:out value="${community.description}" escapeXml="true"/>'
    <c:forEach var="category" items="${community.category}">
    aux.push("${category.toString()}");
    </c:forEach>
    console.log(aux);
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

    let createPill = (selected) => {
        let categoryPills = document.getElementById("categoryPills");
        let pill = document.createElement("div");
        pill.id = selected + 'Pill';
        pill.setAttribute("class", "card border-light flex-row align-items-center justify-content-center me-2 mb-2");
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


    let initializeArray = () => {
    <c:forEach var="category" items="${categories}">
        if(aux.includes("${category}") === false)
            selectArray.push("${category}");
        else
            hiddenArray.push("${category}");
    </c:forEach>
    for (category of aux) {
        createPill(category);
    }
    //sort categories alphabetically
    createSelect();
    }
    initializeArray();


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
</html>
