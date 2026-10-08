package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Admin;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/relation")
public class ShowRelationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Users loggedInUser = session == null ? null : (Users) session.getAttribute("loggedInUser");
        Admin loggedInAdmin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInUser == null && loggedInAdmin == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Authentication required");
            return;
        }

        RelationshipService relationSer = new RelationshipService();
        PrintWriter out = resp.getWriter();
        out.println(relationSer.followersCount());
        out.println(relationSer.friendsCount());

        if (loggedInAdmin != null || (loggedInUser != null && "family".equals(loggedInUser.getRole()))) {
            out.println(relationSer.familyCount());
        }
    }
}
