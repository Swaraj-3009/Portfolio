package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/relation/requestFriend")
public class RequestFriendServlet extends HttpServlet {
    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        RelationshipService relationSer = new RelationshipService();
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        Users loggedInUser = session == null ? null : (Users) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("User authentication required");
            return;
        }

        if(relationSer.requestFriends(loggedInUser)){
            resp.setStatus(201);
            out.println("Friend Request Sent");
        }
        else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Friend Request Not Sent");
        }

    }
}
