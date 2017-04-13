package cz.muni.fi.pv168.web;


import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/UkazkaJSTL"})
public class UkazkaJSTL extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Map<String,Object>> m = new TreeMap<>();

        for(Locale l :Locale.getAvailableLocales()) {
            Map<String, Object> v = new HashMap<>();
            m.put(l.toString(), v);

            v.put("name", l.getDisplayName());
            v.put("origname", l.getDisplayName(l));
            v.put("loc", l);
        }
        request.setAttribute("jazyky", m);

        request.getRequestDispatcher("/stranka.jsp").forward(request, response);
    }

}