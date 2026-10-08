package com.portfolio.Servlets.RelationshiServlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Admin;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/relation")
public class ShowRelationServlet extends HttpServlet {
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        RelationshipService relationSer = new RelationshipService();
        PrintWriter out = resp.getWriter();

        out.println(String.valueOf(relationSer.followersCount()));
        out.println(String.valueOf(relationSer.friendsCount()));

        HttpSession session = req.getSession(false);
        Users loggedInUser = (Users) session.getAttribute("loggedInUser");
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if("family".equals(loggedInUser.getRole()) || loggedInAdmin != null){
            out.println(String.valueOf(relationSer.familyCount()));
        }
    }
}
