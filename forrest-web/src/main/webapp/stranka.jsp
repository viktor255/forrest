<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <c:forEach items="${jazyky}" var="j">
        <tr>
            <td><c:out value="${j.key}"/></td>
            <td><c:out value="${j.value.name}"/></td>
            <td><c:out value="${j.value.origname}"/></td>
            <td><c:out value="${j.value.loc.ISO3Language}"/></td>

        </tr>
    </c:forEach>
</table>

</body>
</html>