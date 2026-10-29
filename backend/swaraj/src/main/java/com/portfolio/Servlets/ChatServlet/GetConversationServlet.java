package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/conversation")
public class GetConversationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ChatServletSupport.requireChatAccess(req, resp)){
            return;
        }
        int conversationId = ChatServletSupport.getIntParameter(req, "conversationId");
        if(conversationId <= 0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "conversationId is required");
            return;
        }

        String peer = ChatServletSupport.requireEligibleConversationPeer(req, resp, conversationId, new ChatService());
        if(peer == null) return;

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"conversationId\":" + conversationId + ",\"peer\":\""
                + ChatServletSupport.escapeJson(peer) + "\"}");
    }
}
