package com.portfolio.Servlets.UserServlet;

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

@WebServlet("/user/register")
public class UserRegisterServlet extends HttpServlet {
    Users user = new Users();
    UserService userSer = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        user.setEmail(req.getParameter("email"));
        user.setRole(req.getParameter("role"));

        PrintWriter out = resp.getWriter();

        if(userSer.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole())){
            resp.setStatus(201);
            out.println("User Registered");

            RequestDispatcher rd = req.getRequestDispatcher("login.html");
            rd.forward(req, resp);
        }
        else{
            resp.setStatus(400);
            out.println("User Not Registered");
        }
        
    }
}
