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
import jakarta.servlet.http.HttpSession;
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


            // resp.setStatus(201);
            // out.println("User logged in successfully");

            // RequestDispatcher rd = req.getRequestDispatcher("UserDashboard.html");
            // rd.forward(req, resp);
        }
        else{
            resp.setStatus(401);
            out.println("Something went wrong");
        }
        
    }
}

//Update User Username
@WebServlet("/user/updateUsername")
class UpdateUserServlet extends HttpServlet{
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

//Update User Password
@WebServlet("/user/updateUserPassword")
class UpdateUserPasswordServlet extends HttpServlet{
    Users user = new Users();
    UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        user.setPassword(req.getParameter("password"));

        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Users loggedInUser = (Users) session.getAttribute("loggedInUser");

        if(userSer.UpdatePassword(loggedInUser, user)){
            resp.setStatus(201);
            out.println("Password Updated");
        }
        else{
            resp.setStatus(401);
            out.println("Something Went Wrong");
        }
    }
}

//Update User Email
@WebServlet("/user/updateUserEmail")
class UpdateUserEmailServlet extends HttpServlet{
    Users user = new Users();
    UserService userSer = new UserService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
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
            resp.setStatus(201);
            out.println("Email Updated");
        }
        else{
            resp.setStatus(401);
            out.println("Something Went Wrong");
        }
    }
}
