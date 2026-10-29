package com.portfolio.Service;

import java.util.List;

import com.portfolio.DAO.ChatDAO;
import com.portfolio.DAO.DaoImpl.ChatDaoImpl;
import com.portfolio.model.Chat;
import com.portfolio.model.ChatConversation;

public class ChatService {
    public boolean accountExists(String username) {
        return username != null && !username.trim().isEmpty()
                && new ChatDaoImpl().accountExists(username.trim());
    }

    public int createConversation(String user1, String user2){
        return new ChatDaoImpl().createConversation(user1, user2);
    }

    public boolean isConversationParticipant(int conversationId, String username){
        return new ChatDaoImpl().isConversationParticipant(conversationId, username);
    }

    public String getConversationPeer(int conversationId, String username){
        return new ChatDaoImpl().getConversationPeer(conversationId, username);
    }

    public List<ChatConversation> getAllConversations(){
        return new ChatDaoImpl().getAllConversations();
    }

    //Send Chat
    public boolean sendChat(Chat chat){
        ChatDAO chatDao = new ChatDaoImpl();
        if(chatDao.sendChat(chat)){
            return true;
        }
        return false;
    }



    //Show Chat
    public List<Chat> ShowChat(Chat chat){
        ChatDAO chatDao =new ChatDaoImpl();

        return chatDao.getChat(chat.getConversationId());
    }



    //edit Chat
    public boolean editChat(Chat chat, Chat newChat){
        ChatDAO chatDao = new ChatDaoImpl();
        newChat.setMessageId(chat.getMessageId());
        newChat.setConversationId(chat.getConversationId());
        newChat.setSender(chat.getSender());
        if(chatDao.editChat(newChat)){
            return true;
        }
        return false;
    }



    //Delete Chat
    public boolean deleteChat(Chat chat){
        ChatDAO chatDao = new ChatDaoImpl();
        if(chatDao.deleteChat(chat)){
            return true;
        }
        return false;
    }

    public boolean editChatAsAdmin(Chat chat){
        return new ChatDaoImpl().editChatAsAdmin(chat);
    }

    public boolean deleteChatAsAdmin(Chat chat){
        return new ChatDaoImpl().deleteChatAsAdmin(chat);
    }
}
