package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.AdminService;
import com.portfolio.model.MySkills;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/skill/updateSkill")
public class UpdateMySkillServlet extends HttpServlet{
    AdminService adminSer = new AdminService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String skillName = req.getParameter("skillName");

        if(skillName == null || skillName.trim().isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().println("Skill name is required");
            return;
        }

        MySkills skill = new MySkills();
        skill.setSkillName(skillName.trim());

        String completedParam = req.getParameter("isCompleted");
        if(completedParam != null) {
            skill.setIsCompleted(Boolean.parseBoolean(completedParam));
        } else {
            skill.setIsCompleted(false);
        }

        PrintWriter out = resp.getWriter();
        if(adminSer.updateMySkill(skill)) {
            resp.setStatus(201);
            out.println("Skill Updated successfully");
        } else {
            resp.setStatus(500);
            out.println("Something Went Wrong");
        }
    }
}
