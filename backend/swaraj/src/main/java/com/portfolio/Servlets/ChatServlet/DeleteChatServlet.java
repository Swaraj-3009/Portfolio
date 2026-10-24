package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;
import com.portfolio.model.Chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/deleteChat")
public class DeleteChatServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        deleteChat(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        deleteChat(req, resp);
    }

    private void deleteChat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sender = ChatServletSupport.getLoggedInUsername(req);
        if(sender == null){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }

        int conversationId = ChatServletSupport.getIntParameter(req, "conversationId");
        long messageId = ChatServletSupport.getLongParameter(req, "messageId");
        if(conversationId <= 0 || messageId <= 0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "conversationId and messageId are required");
            return;
        }

        Chat chat = new Chat();
        chat.setConversationId(conversationId);
        chat.setMessageId(messageId);
        chat.setSender(sender);
        if(!new ChatService().deleteChat(chat)){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found or not owned by the logged-in user");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}