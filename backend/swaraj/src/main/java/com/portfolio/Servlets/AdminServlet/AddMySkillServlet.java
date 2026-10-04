package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;
import com.portfolio.model.MySkills;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/skill/addSkill")
public class AddMySkillServlet extends HttpServlet{
    AdminService adminSer = new AdminService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MySkills skill = new MySkills();
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);

        String skillName = req.getParameter("skillName");
        if (skillName == null || skillName.trim().isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().println("Skill name is required");
            return;
        }
        skill.setSkillName(skillName.trim());

        String completedParam = req.getParameter("isCompleted");
        if (completedParam != null) {
            skill.setIsCompleted(Boolean.parseBoolean(completedParam));
        } else {
            skill.setIsCompleted(false);
        }

        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (adminSer.addMySkill(loggedInAdmin, skill)) {
            resp.setStatus(201);
            out.println("Skill added successfully");
        } else {
            resp.setStatus(500);
            out.println("Something Went Wrong");
        }
    }
}
