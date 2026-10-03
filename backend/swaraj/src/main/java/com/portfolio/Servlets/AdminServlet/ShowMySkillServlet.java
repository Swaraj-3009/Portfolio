package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.util.List;

import com.portfolio.Service.AdminService;
import com.portfolio.model.MySkills;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/skill")
public class ShowMySkillServlet extends HttpServlet {
    AdminService adminSer = new AdminService();
    
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        List<MySkills> skills = adminSer.showMySkill();

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        if (skills == null || skills.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("<h2>No skills found</h2>");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>Skills</h2>");

        for (MySkills skill : skills) {
            html.append("<p><strong>Name:</strong> ")
                .append(skill.getSkillName() == null ? "" : skill.getSkillName())
                .append(" | <strong>Status:</strong> ")
                .append(Boolean.TRUE.equals(skill.getIsCompleted()) ? "Completed" : "Not Completed")
                .append("</p>");
        }

        html.append("</body></html>");
        resp.getWriter().write(html.toString());
    }
}
