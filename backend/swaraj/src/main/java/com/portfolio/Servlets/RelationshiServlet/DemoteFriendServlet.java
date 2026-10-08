package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;

import com.portfolio.Service.RelationshipService;
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
        Users loggedInUser = session == null ? null : (Users) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("User authentication required");
            return;
        }

        RelationshipService service = new RelationshipService();
        if (!"friends".equals(service.relationshipRole(loggedInUser.getUsername()))) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().println("Only an accepted friend can demote themselves");
            return;
        }

        Users user = new Users();
        user.setUsername(loggedInUser.getUsername());
        if (service.demoteFriend(user)) {
            loggedInUser.setRole("follower");
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Friend status removed");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().println("User is no longer a friend");
        }
    }
}