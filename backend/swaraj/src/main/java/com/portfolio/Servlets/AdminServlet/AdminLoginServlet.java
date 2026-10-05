package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//Admin login
@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private final AdminService adminSer = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        Admin admin = new Admin();
        admin.setUsername(req.getParameter("username"));
        admin.setPassword(req.getParameter("password"));

        PrintWriter out = resp.getWriter();

        try {
            Admin loggedInAdmin = adminSer.LoginAdmin(admin);
            HttpSession session = req.getSession(true);
            req.changeSessionId();

            session.setAttribute("loggedInAdmin", loggedInAdmin);
            session.setMaxInactiveInterval(30 * 60);

            resp.setContentType("text/plain;charset=UTF-8");
            out.println("Admin Logged in");
        } catch (Exception e) {
            resp.setStatus(401);
            out.println("Invalid admin credentials");
        }
    }
}
