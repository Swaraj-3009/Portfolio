package com.portfolio.Servlets.UserServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.UserService;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/user/updateUsername")
public class UpdateUsernameServlet extends HttpServlet{
    Users user = new Users();
    UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));

        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Users loggedInUser = (Users) session.getAttribute("loggedInUser");

        if(userSer.UpdateUsername(loggedInUser, user)){
            resp.setStatus(201);
            out.println("Username Updated");
        }
        else{
            resp.setStatus(401);
            out.println("Something Went Wrong");
        }
    }
}
