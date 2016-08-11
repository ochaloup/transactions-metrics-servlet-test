package org.jboss.qa.ochaloup;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.qa.ochaloup.nested.EjbService;


@WebServlet(name="NestedServlet", urlPatterns={"/nested"})
public class NestedServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private EjbService bean;
        
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        bean.dowork();

        PrintWriter out = response.getWriter();
        out.println("Something happened.");
    }
}
