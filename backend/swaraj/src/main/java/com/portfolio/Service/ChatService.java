package com.portfolio.Service;

import java.util.List;

import com.portfolio.DAO.ChatDAO;
import com.portfolio.DAO.DaoImpl.ChatDaoImpl;
import com.portfolio.model.Chat;

public class ChatService {
    public int createConversation(String user1, String user2){
        return new ChatDaoImpl().createConversation(user1, user2);
    }

    public boolean isConversationParticipant(int conversationId, String username){
        return new ChatDaoImpl().isConversationParticipant(conversationId, username);
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
}
