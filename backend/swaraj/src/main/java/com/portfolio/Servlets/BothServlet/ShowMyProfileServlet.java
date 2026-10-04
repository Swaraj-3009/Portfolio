package com.portfolio.Servlets.BothServlet;

import java.io.IOException;

import com.portfolio.Service.BothService;
import com.portfolio.model.MyProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("both/MyProfile")
public class ShowMyProfileServlet extends HttpServlet {
    BothService bothSer = new BothService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        MyProfile profile = bothSer.showProfile();

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
}
