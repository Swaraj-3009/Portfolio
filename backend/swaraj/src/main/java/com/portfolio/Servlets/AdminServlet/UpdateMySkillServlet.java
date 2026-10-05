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

@WebServlet("/admin/skill/updateSkill")
public class UpdateMySkillServlet extends HttpServlet{
    AdminService adminSer = new AdminService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MySkills skill = new MySkills();
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        Admin loggedInAdmin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");

        skill.setId(Integer.parseInt(req.getParameter("id")));
        skill.setSkillName(req.getParameter("skillName"));
        String completed = req.getParameter("isCompleted");
        skill.setIsCompleted(completed == null ? null : Boolean.valueOf(completed));

        if(adminSer.updateMySkill(loggedInAdmin, skill)) {
            resp.setStatus(200);
            out.println("Skill Updated successfully");
        } else {
            resp.setStatus(500);
            out.println("Something Went Wrong");
        }
    }
}
