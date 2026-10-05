package com.portfolio.Servlets.AdminServlet;

import java.io.IOException;

import com.portfolio.Service.AdminService;
import com.portfolio.model.Admin;
import com.portfolio.model.MyProfile;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/myProfile/updateMyProfile")
public class MyProfileServlet extends HttpServlet {
    AdminService adminSer = new AdminService();

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
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("Profile updated");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Profile update failed");
        }
    }
}

