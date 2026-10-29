package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;
import com.portfolio.model.Chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/editChat")
public class EditChatServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        updateChat(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        updateChat(req, resp);
    }

    private void updateChat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(!ChatServletSupport.requireChatAccess(req, resp)){
            return;
        }
        String sender = ChatServletSupport.getLoggedInUsername(req);

        int conversationId = ChatServletSupport.getIntParameter(req, "conversationId");
        long messageId = ChatServletSupport.getLongParameter(req, "messageId");
        String message = req.getParameter("message");
        if(conversationId <= 0 || messageId <= 0 || message == null || message.isBlank()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "conversationId, messageId and message are required");
            return;
        }

        Chat existingChat = new Chat();
        existingChat.setConversationId(conversationId);
        existingChat.setMessageId(messageId);
        existingChat.setSender(sender);
        Chat updatedChat = new Chat();
        updatedChat.setMessage(message.trim());
        if(!new ChatService().editChat(existingChat, updatedChat)){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found or not owned by the logged-in user");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}