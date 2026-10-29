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

@WebServlet("/relation/demoteFriend")
public class DemoteFriendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin loggedInAdmin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");
        Users loggedInUser = session == null ? null : (Users) session.getAttribute("loggedInUser");
        if (loggedInAdmin == null && loggedInUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("User or administrator authentication required");
            return;
        }

        RelationshipService service = new RelationshipService();
        String username;
        if (loggedInAdmin != null) {
            username = req.getParameter("username");
            if (username == null || username.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required");
                return;
            }
        } else {
            username = loggedInUser.getUsername();
            String requestedUsername = req.getParameter("username");
            if (requestedUsername != null && !username.equals(requestedUsername.trim())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Users can only remove their own friend status");
                return;
            }
        }
        if (!"friends".equals(service.relationshipRole(username))) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().println("Only a friend can be demoted to follower");
            return;
        }

        Users user = new Users();
        user.setUsername(username);
        if (service.demoteFriend(user)) {
            if (loggedInUser != null && username.equals(loggedInUser.getUsername())) {
                loggedInUser.setRole("follower");
            }
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("User is now a follower");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().println("User is no longer a friend");
        }
    }
}
