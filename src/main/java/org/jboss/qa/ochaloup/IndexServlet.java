/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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

@WebServlet(name="IndexServlet", urlPatterns={"/"})
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private TestBean bean;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<p>Row count: <b>" + bean.selectCount() + "</b></p>");
        out.println("<p>");
        out.println("do commit: " + a("http://localhost:8080/transaction-metrics/commit") + "</br>");
        out.println("do rollback: " + a("http://localhost:8080/transaction-metrics/rollback") + "</br>");
        out.println("do timeout: " + a("http://localhost:8080/transaction-metrics/timeout") + "</br>");
        out.println("do xa fail: " + a("http://localhost:8080/transaction-metrics/xacommitfail") + "</br>");
        out.println("do xa fail with RMERR: " + a("http://localhost:8080/transaction-metrics/xacommitfailrmerr") + "</br>");
        out.println("</p>");
    }

    private String a(String addr) {
        return String.format("<a href=\"%1$s\">%1$s</a>", addr);
    }
}
