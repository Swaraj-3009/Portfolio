package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.Chat;
import com.portfolio.model.ChatConversation;

public interface ChatDAO {
    public boolean isChatEnabled();
    public boolean setChatEnabled(boolean enabled);
    public boolean accountExists(String username);
    public boolean isAdminAccount(String username);
    public int createConversation(String user1, String user2);
    public boolean isConversationParticipant(int conversationId, String username);
    public String getConversationPeer(int conversationId, String username);
    public List<ChatConversation> getAllConversations();
    public boolean sendChat(Chat chat);
    public List<Chat> getChat(int conversationId);
    public boolean editChat(Chat chat);
    public boolean deleteChat(Chat chat);
    public boolean editChatAsAdmin(Chat chat);
    public boolean deleteChatAsAdmin(Chat chat);
}
