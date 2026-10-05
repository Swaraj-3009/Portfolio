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
    private final UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        PrintWriter out = resp.getWriter();

        try {
            Users loggedInUser = userSer.loginUser(username, password);
            if (loggedInUser != null) {
                HttpSession session = req.getSession(true);
                req.changeSessionId();

                session.setAttribute("loggedInUser", loggedInUser);
                session.setMaxInactiveInterval(30 * 60);

                resp.setContentType("text/plain;charset=UTF-8");
                out.println("User logged in successfully");
            } else {
                resp.setStatus(401);
                out.println("Invalid username or password");
            }
        } catch (RuntimeException e) {
            resp.setStatus(401);
            out.println("Invalid username or password");
        }
    }
}
