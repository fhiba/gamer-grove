<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="Communities.Create"/></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/components/header.jsp" %>
<h1>NEW COMMUNITY:</h1>
<c:url var="communityUrl" value="/new-community" />
<form:form action="${communityUrl}" method="post" modelAttribute="newCommunityForm" id="myForm" onsubmit="addCategoriesToForm()">
    <table>
        <tr>
            <td>Name:</td>
            <td><form:input path="name" /></td>
            <td><form:errors path="name" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td>Description:</td>
            <td><form:input path="description" /></td>
            <td><form:errors path="description" cssStyle="color: red" cssClass="error" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Create!" /></td>
            <td>
                <div id="categoryPills" class="d-flex flex-row">
                </div>
                <div class="d-flex">
                    <button type="button" onclick="showSelect()">add category</button>
                    <div id="selectDiv" hidden="hidden">
                        <label for="select"></label>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <form:errors cssStyle="color: red" cssClass="error" />
    <form:errors cssStyle="color: red" cssClass="error" path="categories" />
</form:form>
</body>
</html>
<script language="JavaScript">
    let hiddenArray = [];
    let selectArray = [];
    let addOptionToSelect = (category) => {
        let select = document.getElementById("select");
        let option = document.createElement("option");
        option.id = category+'Option';
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
        let selectedOption = document.getElementById(selected+'Option');
        selectedOption.remove();

        //create pill
        createPill(selected);
        select.remove();
        createSelect();

        //hide selectDiv
        let selectDiv = document.getElementById("selectDiv");
        selectDiv.hidden = true;
    }

    createSelect = () => {
        console.log("create select")
        let select = document.getElementById("select");
        if(select){
            console.log("select removed")
            select.remove();
        }
        select = document.createElement("select");
        select.id = "select";
        select.onchange = addCategory;
        document.getElementById("selectDiv").appendChild(select);
        let defaultOption = document.createElement("option");
        defaultOption.hidden = true;
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.text = "Categories";
        select.appendChild(defaultOption);
        selectArray.sort((a,b) => a.localeCompare(b));
        for(category of selectArray){
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

    let showSelect = () => {
        let selectDiv = document.getElementById("selectDiv");
        selectDiv.hidden = !selectDiv.hidden;
    };


    let createPill = (selected) => {
        let categoryPills = document.getElementById("categoryPills");
        let pill = document.createElement("div");
        pill.id = selected+'Pill';
        pill.setAttribute("class", " d-flex flex-col");
        let p = document.createElement("h5");
        let span = document.createElement("span");
        span.setAttribute("class", "badge rounded-pill bg-primary");
        span.innerHTML = selected;
        pill.appendChild(p);
        p.appendChild(span);
        categoryPills.appendChild(pill);
        let removeButton = document.createElement("button");
        removeButton.innerHTML = "X";
        removeButton.type = "button";
        removeButton.setAttribute("class", "btn btn-outline-danger");
        removeButton.onclick = () => {
            removeCategory(selected);
        };
        pill.appendChild(removeButton);
    }



    let removeCategory = (category) => {
        //update arrays
        selectArray.push(category);
        hiddenArray = hiddenArray.filter(cat => cat !== category);
        //re-sort categories and reinsert them on select
        createSelect();
        //remove pill

        let pill = document.getElementById(category+'Pill');
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
