package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Admin;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/relation/followers")
public class ShowFollowersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Authentication required");
            return;
        }

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        Users loggedInUser = (Users) session.getAttribute("loggedInUser");
        if (loggedInAdmin == null && loggedInUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Authentication required");
            return;
        }

        String role = loggedInUser == null ? "admin" : loggedInUser.getRole();
        if (loggedInAdmin == null && !"family".equals(role) && !"follower".equals(role)
                && !"followers".equals(role) && !"friends".equals(role)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("You are not allowed to view this follower list");
            return;
        }

        RelationshipService relationSer = new RelationshipService();
        PrintWriter out = resp.getWriter();
        List<Users> followers = relationSer.followers();

        if (followers != null) {
            for (Users follower : followers) {
                out.println(follower.getUsername());
            }
        }
    }
}
