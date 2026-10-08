package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Admin;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/relation/addFamily")
public class AddFriendToFamily extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin loggedInAdmin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Admin authentication required");
            return;
        }

        String username = req.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Username is required");
            return;
        }

        Users user = new Users();
        user.setUsername(username.trim());

        if (new RelationshipService().addFamily(user)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(user.getUsername() + " is now family");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Not Promoted to Family");
        }
    }
}
