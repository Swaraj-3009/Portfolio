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

@WebServlet("/admin/skill/DeleteSkill")
public class DeleteMySkillServlet extends HttpServlet{
    AdminService adminSer = new AdminService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MySkills skill = new MySkills();
        PrintWriter out = resp.getWriter();
    
        skill.setId(Integer.parseInt(req.getParameter("id")));
        
        if(adminSer.deleteMySkill(skill)){
            resp.setStatus(201);
            out.println("Skill Deleted Successfully");
        }
        else{
            resp.setStatus(500);
            out.println("Skill Deleted Successfully");
        }
    }
}
