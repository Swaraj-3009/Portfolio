package com.portfolio.Servlets.UserServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/user/register")
public class UserRegisterServlet extends HttpServlet {
    private final UserService userSer = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        PrintWriter out = resp.getWriter();

        if(userSer.registerUser(username, password, email, "follower")){
            resp.setStatus(201);
            out.println("User Registered");
        }
        else{
            resp.setStatus(400);
            out.println("User Not Registered");
        }
        
    }
}
