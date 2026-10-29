package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;
import java.util.List;

import com.portfolio.Service.RelationshipService;
import com.portfolio.model.Admin;
import com.portfolio.model.Chat;
import com.portfolio.model.ChatConversation;
import com.portfolio.model.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

final class ChatServletSupport {
    private ChatServletSupport() {}

    static boolean requireChatAccess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if(session == null){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return false;
        }

        if(session.getAttribute("loggedInAdmin") instanceof Admin){
            return requireChatFeatureEnabled(resp);
        }
        Users user = (Users) session.getAttribute("loggedInUser");
        if(user == null){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return false;
        }

        String role = new RelationshipService().relationshipRole(user.getUsername());
        if(!isChatRoleAllowed(role)){
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only friends and family can use chat");
            return false;
        }
        return requireChatFeatureEnabled(resp);
    }

    static boolean requireChatFeatureEnabled(HttpServletResponse resp) throws IOException {
        if(new com.portfolio.Service.ChatService().isChatEnabled()){
            return true;
        }
        resp.sendError(HttpServletResponse.SC_LOCKED, "Chat is disabled by the administrator");
        return false;
    }

    static boolean isChatRoleAllowed(String role) {
        return "friends".equals(role) || "family".equals(role);
    }

    static String getLoggedInUsername(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if(session == null){
            return null;
        }
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        Users user = (Users) session.getAttribute("loggedInUser");
        return admin != null ? admin.getUsername() : user != null ? user.getUsername() : null;
    }

    static String getLoggedInAdminUsername(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if(session == null){
            return null;
        }
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        return admin == null ? null : admin.getUsername();
    }

    static int getIntParameter(HttpServletRequest req, String name) {
        try {
            return Integer.parseInt(req.getParameter(name));
        } catch (RuntimeException e) {
            return 0;
        }
    }

    static long getLongParameter(HttpServletRequest req, String name) {
        try {
            return Long.parseLong(req.getParameter(name));
        } catch (RuntimeException e) {
            return 0;
        }
    }

    static void writeChatsJson(HttpServletResponse resp, List<Chat> chats) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder json = new StringBuilder("[");
        for(int index = 0; index < chats.size(); index++){
            Chat chat = chats.get(index);
            if(index > 0){
                json.append(',');
            }
            json.append("{\"messageId\":").append(chat.getMessageId())
                    .append(",\"conversationId\":").append(chat.getConversationId())
                    .append(",\"message\":\"").append(escapeJson(chat.getMessage()))
                    .append("\",\"sender\":\"").append(escapeJson(chat.getSender()))
                    .append("\",\"reciever\":\"").append(escapeJson(chat.getReciever()))
                    .append("\",\"time\":\"").append(escapeJson(String.valueOf(chat.getTime())))
                    .append("\"}");
        }
        resp.getWriter().write(json.append(']').toString());
    }

    static void writeConversationsJson(HttpServletResponse resp, List<ChatConversation> conversations) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder json = new StringBuilder("[");
        for(int index = 0; index < conversations.size(); index++){
            ChatConversation conversation = conversations.get(index);
            if(index > 0){
                json.append(',');
            }
            json.append("{\"conversationId\":").append(conversation.getConversationId())
                    .append(",\"user1\":\"").append(escapeJson(conversation.getUser1()))
                    .append("\",\"user2\":\"").append(escapeJson(conversation.getUser2())).append("\"}");
        }
        resp.getWriter().write(json.append(']').toString());
    }

    static String escapeJson(String value) {
        if(value == null){
            return "";
        }
        StringBuilder escaped = new StringBuilder();
        for(int index = 0; index < value.length(); index++){
            char character = value.charAt(index);
            switch(character){
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if(character < 0x20){
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
            }
        }
        return escaped.toString();
    }
}