package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.util.List;

import com.portfolio.Service.UserService;
import com.portfolio.model.Admin;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/users/*")
public class AdminUsersServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!requireAdmin(req, resp)) return;
        List<Users> users = userService.getAllUsers();
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        StringBuilder json = new StringBuilder("[");
        for(int i = 0; i < users.size(); i++){
            if(i > 0) json.append(',');
            Users user = users.get(i);
            json.append("{\"username\":\"").append(escapeJson(user.getUsername()))
                    .append("\",\"email\":\"").append(escapeJson(user.getEmail()))
                    .append("\",\"role\":\"").append(escapeJson(user.getRole())).append("\"}");
        }
        resp.getWriter().write(json.append(']').toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!requireAdmin(req, resp)) return;
        if(!"/delete".equals(req.getPathInfo())){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String username = req.getParameter("username");
        if(username == null || username.isBlank()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "username is required");
            return;
        }
        try {
            if(!userService.deleteUser(username)){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User account was not found");
                return;
            }
        } catch (IllegalStateException e) {
            getServletContext().log("Could not remove user account " + username, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User account could not be removed");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Admin admin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");
        if(admin != null && admin.getUsername() != null && !admin.getUsername().isBlank()) return true;
        resp.sendError(session != null && session.getAttribute("loggedInUser") != null
                ? HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED,
                "Administrator access required");
        return false;
    }

    private String escapeJson(String value) {
        if(value == null) return "";
        StringBuilder escaped = new StringBuilder();
        for(int i = 0; i < value.length(); i++){
            char c = value.charAt(i);
            switch(c){
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if(c < 0x20) escaped.append(String.format("\\u%04x", (int)c));
                    else escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
