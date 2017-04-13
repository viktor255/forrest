<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<%! //kód mimo metodu service()
    public String prictiJedna(String a) {
        return Integer.toString(Integer.parseInt(a)+1);
    }
%>
<html>
<body>
<%  //kód uvnitř metody service()
    if(request.getMethod().equals("GET")) {
%>
<form method="post">
    Zadejte číslo: <input name="cislo" value="">
    <input type="submit" >
</form>
<%
} else if (request.getMethod().equals("POST")) {
    String cislo = request.getParameter("cislo");
    String plusjedna = prictiJedna(cislo);
%>
Výsledek <%out.println(cislo);%> + 1 je <%=plusjedna%>
<%
    }
%>
</body>
</html>