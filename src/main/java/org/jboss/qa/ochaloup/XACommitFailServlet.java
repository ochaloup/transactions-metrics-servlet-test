package org.jboss.qa.ochaloup;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.qa.ochaloup.ejb.TestBean;


@WebServlet(name="XACommitFailServlet", urlPatterns={"/xacommitfail"})
public class XACommitFailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private TestBean bean;
        
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        bean.doXAFail();

        PrintWriter out = response.getWriter();
        out.println("XA commit fail - count of rows: " + bean.selectCount());
    }
}
