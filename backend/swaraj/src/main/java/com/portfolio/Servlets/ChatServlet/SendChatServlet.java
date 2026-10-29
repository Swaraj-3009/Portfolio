package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;
import java.time.LocalDateTime;

import com.portfolio.Service.ChatService;
import com.portfolio.model.Chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/sendChat")
public class SendChatServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ChatServletSupport.requireChatAccess(req, resp)){
            return;
        }
        String sender = ChatServletSupport.getLoggedInUsername(req);

        int conversationId = ChatServletSupport.getIntParameter(req, "conversationId");
        String message = req.getParameter("message");
        String reciever = req.getParameter("reciever");
        if(reciever == null){
            reciever = req.getParameter("receiver");
        }
        if(conversationId <= 0 || message == null || message.isBlank() || reciever == null || reciever.isBlank()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "conversationId, message and reciever are required");
            return;
        }

        Chat chat = new Chat();
        chat.setConversationId(conversationId);
        chat.setMessage(message.trim());
        chat.setSender(sender);
        chat.setReciever(reciever.trim());
        chat.setTime(LocalDateTime.now());

        if(!new ChatService().sendChat(chat)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message could not be sent to that conversation");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"messageId\":" + chat.getMessageId() + ",\"conversationId\":" + conversationId + "}");
    }
}