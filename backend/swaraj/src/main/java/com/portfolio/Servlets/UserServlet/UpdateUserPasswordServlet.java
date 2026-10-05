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

@WebServlet("/user/updateUserPassword")
public class UpdateUserPasswordServlet extends HttpServlet{
    private final UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");

        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Users loggedInUser = (Users) session.getAttribute("loggedInUser");

        if(userSer.UpdatePassword(loggedInUser, currentPassword, newPassword)){
            loggedInUser.setPassword(newPassword);
            resp.setStatus(201);
            out.println("Password Updated");
        }
        else{
            resp.setStatus(401);
            out.println("Something Went Wrong");
        }
    }
}
