package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;
import com.portfolio.model.MyProfile;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/myProfile")
public class MyProfileServlet extends HttpServlet {
    AdminService adminSer = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MyProfile profile = adminSer.ShowProfile();

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        if (profile == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("<h2>Profile not found</h2>");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);

        String html = """
                <html>
                  <body>
                    <h2>%s</h2>
                    <p><strong>Email:</strong> %s</p>
                    <p><strong>GitHub:</strong> %s</p>
                    <p><strong>LinkedIn:</strong> %s</p>
                    <p><strong>Phone:</strong> %s</p>
                    <p><strong>Address:</strong> %s</p>
                    <p><strong>About:</strong> %s</p>
                    <img src='%s' alt='Profile image' />
                  </body>
                </html>
                """.formatted(
                profile.getName() == null ? "" : profile.getName(),
                profile.getEmail() == null ? "" : profile.getEmail(),
                profile.getGithubURL() == null ? "" : profile.getGithubURL(),
                profile.getLinkedinURL() == null ? "" : profile.getLinkedinURL(),
                profile.getPhone() == null ? "" : profile.getPhone(),
                profile.getAddress() == null ? "" : profile.getAddress(),
                profile.getAboutMe() == null ? "" : profile.getAboutMe(),
                profile.getProfileImage() == null ? "" : profile.getProfileImage()
        );

        resp.getWriter().write(html);
    }

    //update profile
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MyProfile profile = new MyProfile();
        profile.setName(req.getParameter("name"));
        profile.setAddress(req.getParameter("address"));
        profile.setAboutMe(req.getParameter("aboutMe"));
        profile.setEmail(req.getParameter("email"));
        profile.setGithubURL(req.getParameter("githubURL"));
        profile.setLinkedinURL(req.getParameter("linkedinURL"));
        profile.setPhone(req.getParameter("phone"));
        profile.setProfileImage(req.getParameter("profileImage"));

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedInAdmin") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if(adminSer.UpdateAdminProfile(loggedInAdmin, profile)){
            resp.setStatus(HttpServletResponse.SC_OK);
            RequestDispatcher rd = req.getRequestDispatcher("AdminDashboard.html");
            rd.forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Profile update failed");
        }
    }
}

