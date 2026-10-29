package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/conversations")
public class ShowConversationsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ChatServletSupport.requireChatAccess(req, resp)) return;
        String username = ChatServletSupport.getLoggedInUsername(req);
        if(username == null || username.isBlank()){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }
        ChatServletSupport.writeConversationsJson(resp, new ChatService().getConversationsForUser(username));
    }
}
