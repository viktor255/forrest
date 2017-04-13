package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.forrest.Tree;
import cz.muni.fi.pv168.forrest.TreeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for managing trees.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@WebServlet(TreeServlet.URL_MAPPING + "/*")
public class TreeServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/trees";

    private final static Logger log = LoggerFactory.getLogger(TreeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showTreesList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //support non-ASCII characters in form
        request.setCharacterEncoding("utf-8");
        //action specified by pathInfo
        String action = request.getPathInfo();
        log.debug("POST ... {}",action);
        switch (action) {
            case "/add":
                //getting POST parameters from form
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String type = request.getParameter("type");
                String isProtected = request.getParameter("protected");
                //form data validity check
                if (name == null || name.length() == 0 || type == null || type.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty !");
                    log.debug("form data invalid");
                    showTreesList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                   // Tree tree = new Tree(null, name, author);
                    Tree tree = new Tree(Long.decode(id),name,type,true);
                    getTreeManager().createTree(tree);
                    //redirect-after-POST protects from multiple submission
                    log.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    log.error("Cannot add tree", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long treeId = Long.valueOf(request.getParameter("id"));
                    getTreeManager().deleteTree(getTreeManager().getTree(treeId));
                    log.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    log.error("Cannot delete tree", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //TODO
                return;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    /**
     * Gets TreeManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return TreeManager instance
     */
    private TreeManager getTreeManager() {
        return (TreeManager) getServletContext().getAttribute("treeManager");
    }

    /**
     * Stores the list of trees to request attribute "trees" and forwards to the JSP to display it.
     */
    private void showTreesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("showing table of trees");
            request.setAttribute("trees", getTreeManager().findAllTrees());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            log.error("Cannot show trees", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
