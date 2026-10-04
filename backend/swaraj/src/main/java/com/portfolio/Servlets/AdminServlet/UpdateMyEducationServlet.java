package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;
import com.portfolio.model.MyEducation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/myEducation/updateEducation")
public class UpdateMyEducationServlet extends HttpServlet {
    AdminService adminSer = new AdminService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MyEducation education = new MyEducation();
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        education.setId(Integer.parseInt(req.getParameter("id")));
        education.setDegree(req.getParameter("degree"));
        education.setInstitution(req.getParameter("institution"));
        education.setYearofPassing(req.getParameter("yearOfPassing"));
        education.setGrade(req.getParameter("grade"));
        education.setDescription(req.getParameter("description"));

        if(adminSer.updateMyEducation(loggedInAdmin, education)){
            resp.setStatus(201);
            out.write("Education Updated");
        }
        else{
            resp.setStatus(500);
            out.write("Something went Wrong");
        }
    }
}
