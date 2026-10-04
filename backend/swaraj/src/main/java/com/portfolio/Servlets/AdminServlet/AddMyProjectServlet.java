package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;
import com.portfolio.model.MyProject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/myProject/addMyProject")
public class AddMyProjectServlet extends HttpServlet{
    AdminService adminSer = new AdminService();
    
    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MyProject project = new MyProject();
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        project.setId(Integer.parseInt(req.getParameter("id")));
        project.setProjectName(req.getParameter("projectName"));
        project.setProjectDescription(req.getParameter("projectDescription"));
        project.setTechnologiesUsed(req.getParameter("technologiesUsed"));
        project.setGithubURL(req.getParameter("githubUrl"));
        project.setLiveURL(req.getParameter("liveUrl"));
        project.setIsCompleted(Boolean.parseBoolean(req.getParameter("isCompleted")));

        if(adminSer.addProject(loggedInAdmin, project)){
            resp.setStatus(201);
            out.write("Project Added");
        }
        else{
            resp.setStatus(500);
            out.write("Project Not Added");
        }
    }
}
