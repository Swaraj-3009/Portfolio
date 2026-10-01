package com.portfolio.Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("admin/login")
public class AdminServlet extends HttpServlet {
    Admin admin = new Admin();
    AdminService adminSer = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        admin.setUsername(req.getParameter("username"));
        admin.setPassword(req.getParameter("password"));

        adminSer.LoginAdmin(admin);
        resp.setStatus(201);
        PrintWriter out = resp.getWriter();
        out.println("Admin Logged in");

        RequestDispatcher rd = req.getRequestDispatcher("AdminDashboard.html");
        rd.forward(req, resp);
    }
}
