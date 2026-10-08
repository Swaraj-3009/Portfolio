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

@WebServlet("/relation/cancelFriendRequest")
public class CancelFriendRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin loggedInAdmin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");
        Users loggedInUser = session == null ? null : (Users) session.getAttribute("loggedInUser");
        RelationshipService service = new RelationshipService();
        String username;

        if (loggedInAdmin != null) {
            username = req.getParameter("username");
            if (username == null || username.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Username is required");
                return;
            }
        } else if (loggedInUser != null
                && "requestedFriends".equals(service.relationshipRole(loggedInUser.getUsername()))) {
            username = loggedInUser.getUsername();
            String requestedUsername = req.getParameter("username");
            if (requestedUsername != null && !username.equals(requestedUsername.trim())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().println("You can only cancel your own friend request");
                return;
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().println("A pending requester or admin session is required");
            return;
        }

        Users user = new Users();
        user.setUsername(username.trim());
        if (service.cancelFriendRequest(user)) {
            if (loggedInUser != null) {
                loggedInUser.setRole("follower");
            }
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Friend request cancelled");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().println("No pending friend request exists for this user");
        }
    }
}