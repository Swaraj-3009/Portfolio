package com.portfolio.Servlets.BothServlet;

import java.io.IOException;

import com.portfolio.Service.BothService;
import com.portfolio.model.MyProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/both/MyProfile")
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

        String imageUrl = profile.getProfileImage() == null ? "" : profile.getProfileImage();
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
                    <p><strong>Profile image:</strong> %s</p>
                    <img src='%s' alt='Profile image' />
                  </body>
                </html>
                """.formatted(
                escapeHtml(profile.getName() == null ? "" : profile.getName()),
                escapeHtml(profile.getEmail() == null ? "" : profile.getEmail()),
                escapeHtml(profile.getGithubURL() == null ? "" : profile.getGithubURL()),
                escapeHtml(profile.getLinkedinURL() == null ? "" : profile.getLinkedinURL()),
                escapeHtml(profile.getPhone() == null ? "" : profile.getPhone()),
                escapeHtml(profile.getAddress() == null ? "" : profile.getAddress()),
                escapeHtml(profile.getAboutMe() == null ? "" : profile.getAboutMe()),
                escapeHtml(imageUrl),
                escapeHtml(imageUrl)
        );

        resp.getWriter().write(html);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
