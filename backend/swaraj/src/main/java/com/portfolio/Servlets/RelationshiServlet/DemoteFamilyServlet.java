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

@WebServlet("/relation/demoteFamily")
public class DemoteFamilyServlet extends HttpServlet {
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
                && "family".equals(service.relationshipRole(loggedInUser.getUsername()))) {
            username = loggedInUser.getUsername();
            String requestedUsername = req.getParameter("username");
            if (requestedUsername != null && !username.equals(requestedUsername.trim())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().println("Family members can only demote themselves");
                return;
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().println("Family or admin authentication required");
            return;
        }

        Users user = new Users();
        user.setUsername(username.trim());
        if (service.demoteFamily(user)) {
            if (loggedInUser != null) {
                loggedInUser.setRole("friends");
            }
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Family membership removed");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().println("User is not a family member");
        }
    }
}