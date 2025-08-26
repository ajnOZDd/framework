package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest rq, HttpServletResponse rs) 
            throws IOException, ServletException {
        processRequest(rq, rs);
    }
    
    @Override
    protected void doPost(HttpServletRequest rq, HttpServletResponse rs) 
            throws IOException, ServletException {
        processRequest(rq, rs);
    }
    
    public void processRequest(HttpServletRequest rq, HttpServletResponse rs) 
            throws IOException, ServletException {
        rs.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = rs.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><body>");
            out.println("<h1>FrontController fonctionne!</h1>");
            out.println("<p>URL: " + rq.getRequestURL() + "</p>");
            out.println("</body></html>");
        }
    }
}