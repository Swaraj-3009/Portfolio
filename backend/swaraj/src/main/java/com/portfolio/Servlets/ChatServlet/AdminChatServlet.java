package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;

import com.portfolio.Service.ChatService;
import com.portfolio.model.Chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/chat/*")
public class AdminChatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!requireAdmin(req, resp)){
            return;
        }

        ChatService chatService = new ChatService();
        String path = req.getPathInfo();
        if("/settings".equals(path)){
            writeFeatureStatus(resp, chatService.isChatEnabled());
            return;
        }
        if(!ChatServletSupport.requireChatFeatureEnabled(resp)){
            return;
        }
        if("/conversations".equals(path)){
            ChatServletSupport.writeConversationsJson(resp, chatService.getAllConversations());
            return;
        }
        if("/messages".equals(path)){
            int conversationId = ChatServletSupport.getIntParameter(req, "conversationId");
            if(conversationId <= 0){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "conversationId is required");
                return;
            }
            Chat chat = new Chat();
            chat.setConversationId(conversationId);
            ChatServletSupport.writeChatsJson(resp, chatService.ShowChat(chat));
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!requireAdmin(req, resp)){
            return;
        }

        String path = req.getPathInfo();
        if("/settings".equals(path)){
            String enabled = req.getParameter("enabled");
            if(!"true".equalsIgnoreCase(enabled) && !"false".equalsIgnoreCase(enabled)){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "enabled must be true or false");
                return;
            }
            if(!new ChatService().setChatEnabled(Boolean.parseBoolean(enabled))){
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Chat setting could not be saved");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        if(!ChatServletSupport.requireChatFeatureEnabled(resp)){
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
        ChatService chatService = new ChatService();
        if("/edit".equals(path)){
            String message = req.getParameter("message");
            if(message == null || message.isBlank()){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "message is required");
                return;
            }
            chat.setMessage(message.trim());
            if(!chatService.editChatAsAdmin(chat)){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found in this conversation");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        if("/delete".equals(path)){
            if(!chatService.deleteChatAsAdmin(chat)){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found in this conversation");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(ChatServletSupport.getLoggedInAdminUsername(req) != null){
            return true;
        }
        HttpSession session = req.getSession(false);
        int status = session != null && session.getAttribute("loggedInUser") != null
                ? HttpServletResponse.SC_FORBIDDEN
                : HttpServletResponse.SC_UNAUTHORIZED;
        resp.sendError(status, "Administrator access required");
        return false;
    }

    private void writeFeatureStatus(HttpServletResponse resp, boolean enabled) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write("{\"enabled\":" + enabled + "}");
    }
}
