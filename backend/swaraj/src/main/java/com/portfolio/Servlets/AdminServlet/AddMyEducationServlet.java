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

@WebServlet("/admin/myEducation/AddEducation")
public class AddMyEducationServlet extends HttpServlet {
    AdminService adminSer = new AdminService();

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MyEducation education = new MyEducation();
        PrintWriter out = resp.getWriter();

        Admin loggedInAdmin = AdminServletSupport.requireAdmin(req, resp);
        if(loggedInAdmin == null) return;

        String degree = req.getParameter("degree");
        String institution = req.getParameter("institution");
        if(degree == null || degree.isBlank() || institution == null || institution.isBlank()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Degree and institution are required");
            return;
        }
        education.setDegree(degree.trim());
        education.setInstitution(institution.trim());
        education.setYearofPassing(req.getParameter("yearOfPassing"));
        education.setGrade(req.getParameter("grade"));
        education.setDescription(req.getParameter("description"));

        if(adminSer.addMyEducation(loggedInAdmin, education)){
            resp.setStatus(201);
            out.write("Education Added");
        }
        else{
            resp.setStatus(500);
            out.write("Education not added");
        }
    }
}
