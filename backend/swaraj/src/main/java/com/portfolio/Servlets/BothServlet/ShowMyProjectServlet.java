package com.portfolio.Servlets.BothServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.portfolio.Service.BothService;
import com.portfolio.model.MyProject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/myProject")
public class ShowMyProjectServlet extends HttpServlet {
    BothService bothSer = new BothService();

    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        PrintWriter out = resp.getWriter();
        List<MyProject> projects = bothSer.showMyProject();

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        

        if (projects == null || projects.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("<h2>Education not found</h2>");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        StringBuilder html = new StringBuilder();
        html.append("<html><body><h2>Projects</h2>");

        for (MyProject project : projects) {
            html.append("<p>")
                .append("<strong>Project:</strong> ")
                .append(project.getProjectName() == null ? "" : project.getProjectName())
                .append(" | <strong>Description:</strong> ")
                .append(project.getProjectDescription() == null ? "" : project.getProjectDescription())
                .append(" | <strong>Technologies:</strong> ")
                .append(project.getTechnologiesUsed() == null ? "" : project.getTechnologiesUsed())
                .append(" | <strong>GitHub:</strong> ")
                .append(project.getGithubURL() == null ? "" : project.getGithubURL())
                .append(" | <strong>Live URL:</strong> ")
                .append(project.getLiveURL() == null ? "" : project.getLiveURL())
                .append("</p>");
        }

        html.append("</body></html>");
        out.print(html);
    }
}
