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

@WebServlet("/user/login")
public class UserLoginServlet extends HttpServlet{
    Users user = new Users();
    UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        user.setEmail(req.getParameter("email"));
        user.setRole(req.getParameter("role"));

        PrintWriter out = resp.getWriter();

        if(userSer.loginUser(user.getUsername(), user.getPassword()) != null){
            // Get or create session
            HttpSession session = req.getSession(true);
            req.changeSessionId();

            // Store logged-in user's details
            session.setAttribute("loggedInUser", user);

            // Optional: set session timeout (30 minutes)
            session.setMaxInactiveInterval(30 * 60);

            // Redirect to dashboard
            resp.sendRedirect(req.getContextPath() + "/UserDashboard.html");

        }
        else{
            resp.setStatus(401);
            out.println("Something went wrong");
        }
    }
}
