package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/createChat")
public class CreateChatServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ChatServletSupport.requireChatAccess(req, resp)){
            return;
        }
        String sender = ChatServletSupport.getLoggedInUsername(req);

        String reciever = req.getParameter("reciever");
        if(reciever == null){
            reciever = req.getParameter("receiver");
        }
        if(reciever == null || reciever.isBlank() || sender.equalsIgnoreCase(reciever.trim())){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "A different reciever is required");
            return;
        }

        ChatService chatService = new ChatService();
        String normalizedReciever = reciever.trim();
        if (!chatService.accountExists(normalizedReciever)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recipient account was not found");
            return;
        }

        int conversationId = chatService.createConversation(sender, normalizedReciever);
        if(conversationId <= 0){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Conversation could not be created");
            return;
        }
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"conversationId\":" + conversationId + "}");
    }
}
