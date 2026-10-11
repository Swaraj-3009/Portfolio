package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.Chat;

public interface ChatDAO {
    public boolean sendChat(Chat chat);
    public List<Chat> getChat(int conversationId);
    public boolean editChat(Chat chat);
    public boolean deleteChat(Chat chat);
}
