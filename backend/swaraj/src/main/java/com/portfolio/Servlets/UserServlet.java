package com.portfolio.Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.UserService;
import com.portfolio.model.Users;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

//register
@WebServlet("/user/register")
public class UserServlet extends HttpServlet {
    Users user = new Users();
    UserService userSer = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        user.setEmail(req.getParameter("email"));
        user.setRole(req.getParameter("role"));

        userSer.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole());
        resp.setStatus(201);
        PrintWriter out = resp.getWriter();
        out.println("User Registered");

        RequestDispatcher rd = req.getRequestDispatcher("login.html");
        rd.forward(req, resp);
    }
}

//login
@WebServlet("/user/login")
class userLoginServlet extends HttpServlet{
    Users user = new Users();
    UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        user.setEmail(req.getParameter("email"));
        user.setRole(req.getParameter("role"));

        userSer.loginUser(user.getUsername(), user.getPassword());
        PrintWriter out = resp.getWriter();
        out.println("User logged in successfully");
    }
}
