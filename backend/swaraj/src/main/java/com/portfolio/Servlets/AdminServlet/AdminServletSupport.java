package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;

import com.portfolio.model.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

final class AdminServletSupport {
    private AdminServletSupport() {}

    static Admin requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Object value = session == null ? null : session.getAttribute("loggedInAdmin");
        if(value instanceof Admin admin && admin.getUsername() != null && !admin.getUsername().isBlank()){
            return admin;
        }
        int status = session != null && session.getAttribute("loggedInUser") != null
                ? HttpServletResponse.SC_FORBIDDEN
                : HttpServletResponse.SC_UNAUTHORIZED;
        resp.sendError(status, "Administrator authentication required");
        return null;
    }

    static Integer parsePositiveId(String rawValue) {
        if(rawValue == null || rawValue.isBlank()) return null;
        try {
            int id = Integer.parseInt(rawValue.trim());
            return id > 0 ? id : null;
        } catch(NumberFormatException e){
            return null;
        }
    }
}
