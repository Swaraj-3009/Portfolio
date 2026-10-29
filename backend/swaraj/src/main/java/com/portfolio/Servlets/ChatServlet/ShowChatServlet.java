package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;
import com.portfolio.model.Chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat/showChat")
public class ShowChatServlet extends HttpServlet {
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
        ChatService chatService = new ChatService();
        if (ChatServletSupport.requireEligibleConversationPeer(req, resp, conversationId, chatService) == null) return;

        Chat chat = new Chat();
        chat.setConversationId(conversationId);
        ChatServletSupport.writeChatsJson(resp, chatService.ShowChat(chat));
    }
}
