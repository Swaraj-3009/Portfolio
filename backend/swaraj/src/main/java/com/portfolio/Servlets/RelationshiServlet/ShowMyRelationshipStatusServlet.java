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

@WebServlet("/relation/myStatus")
public class ShowMyRelationshipStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Users loggedInUser = session == null ? null : (Users) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("User authentication required");
            return;
        }

        String role = new RelationshipService().relationshipRole(loggedInUser.getUsername());
        if (role == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("Relationship status not found");
            return;
        }

        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println(role);
    }
}