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

@WebServlet("/user/updateUserEmail")
public class UpdateUserEmailServlet extends HttpServlet{
    private final UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        Users user = new Users();
        user.setEmail(req.getParameter("email"));
        user.setPassword(req.getParameter("password"));

        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Users loggedInUser = (Users) session.getAttribute("loggedInUser");

        if(userSer.UpdateEmail(loggedInUser, user)){
            loggedInUser.setEmail(user.getEmail());
            resp.setStatus(201);
            out.println("Email Updated");
        }
        else{
            resp.setStatus(401);
            out.println("Something Went Wrong");
        }
    }
}
