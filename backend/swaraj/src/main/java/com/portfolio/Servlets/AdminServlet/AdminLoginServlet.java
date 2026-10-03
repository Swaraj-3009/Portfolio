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
    Admin admin = new Admin();
    AdminService adminSer = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        admin.setUsername(req.getParameter("username"));
        admin.setPassword(req.getParameter("password"));

        PrintWriter out = resp.getWriter();
        
        if(adminSer.LoginAdmin(admin) != null){
            HttpSession session = req.getSession(true);
            req.changeSessionId();

            session.setAttribute("loggedInAdmin", admin);
            session.setMaxInactiveInterval(30 * 60);

            resp.sendRedirect(req.getContextPath() + "/AdminDashboard.html");
        }
        else{
            resp.setStatus(401);
            out.println("Something Went Wrong");
        } 
    }
}