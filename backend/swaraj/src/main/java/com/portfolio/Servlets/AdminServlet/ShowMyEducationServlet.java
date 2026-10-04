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

@WebServlet("/admin/myEducation")
public class ShowMyEducationServlet extends HttpServlet{
    AdminService adminSer = new AdminService();

    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        MyEducation education = new MyEducation();

        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                education.setId(Integer.parseInt(idParam));
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid education id");
                return;
            }
        }

        MyEducation result = adminSer.showMyEducation(loggedInAdmin, education);

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if (result == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("<h2>Education not found</h2>");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        String html = """
            <html>
            <body>
                <h2>Education Details</h2>
                <p><strong>Degree:</strong> %s</p>
                <p><strong>Institution:</strong> %s</p>
                <p><strong>Year of Passing:</strong> %s</p>
                <p><strong>Grade:</strong> %s</p>
                <p><strong>Description:</strong> %s</p>
            </body>
            </html>
            """.formatted(
                result.getDegree() == null ? "" : result.getDegree(),
                result.getInstitution() == null ? "" : result.getInstitution(),
                result.getYearofPassing() == null ? "" : result.getYearofPassing(),
                result.getGrade() == null ? "" : result.getGrade(),
                result.getDescription() == null ? "" : result.getDescription()
            );
        out.print(html);
    }
}
