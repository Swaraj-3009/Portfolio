package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Admin;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/relation/requestedFriend")
public class ShowRequestedFriendServlet extends HttpServlet{
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        HttpSession session = req.getSession(false);
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Admin authentication required");
            return;
        }
        
        RelationshipService relationSer = new RelationshipService();
        PrintWriter out = resp.getWriter();
        List<Users> requestedFriends = relationSer.requestedFriends();

        if(requestedFriends != null){
            for(Users requestedFriend : requestedFriends){
                out.println(requestedFriend.getUsername());
            }
        }
    }
}
