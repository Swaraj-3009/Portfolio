package com.portfolio.Servlets.BothServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.portfolio.Service.BothService;
import com.portfolio.model.MyEducation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/myEducation")
public class ShowMyEducationServlet extends HttpServlet{
    BothService bothSer = new BothService();

    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        PrintWriter out = resp.getWriter();
        List<MyEducation> educations = bothSer.showMyEducation();

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        

        if (educations == null || educations.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("<h2>Education not found</h2>");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>Education</h2>");

        for (MyEducation education : educations) {
            html.append("<p>")
                .append("<strong>ID:</strong> ")
                .append(education.getId())
                .append(" | ")
                .append("<strong>Degree:</strong> ")
                .append(education.getDegree() == null ? "" : education.getDegree())
                .append(" | <strong>Institution:</strong> ")
                .append(education.getInstitution() == null ? "" : education.getInstitution())
                .append(" | <strong>Year of Passing:</strong> ")
                .append(education.getYearofPassing() == null ? "" : education.getYearofPassing())
                .append(" | <strong>Grade:</strong> ")
                .append(education.getGrade() == null ? "" : education.getGrade())
                .append(" | <strong>Description:</strong> ")
                .append(education.getDescription() == null ? "" : education.getDescription())
                .append("</p>");
        }

        html.append("</body></html>");
        out.print(html);
    }
}
