package com.portfolio.Service;

import java.util.List;

import com.portfolio.DAO.ChatDAO;
import com.portfolio.DAO.DaoImpl.ChatDaoImpl;
import com.portfolio.model.Chat;

public class ChatService {
    public boolean sendChat(Chat chat){
        ChatDAO chatDao = new ChatDaoImpl();
        if(chatDao.sendChat(chat)){
            return true;
        }
        return false;
    }
    public boolean editChat(){
        return false;
    }
    public boolean deleteChat(){
        return false;
    }
    public List<String> ShowChat(){
        return null;
    }
}
