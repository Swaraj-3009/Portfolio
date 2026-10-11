package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.ChatDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Chat;

public class ChatDaoImpl implements ChatDAO{

    @Override
    public boolean sendChat(Chat chat) {
        String sql = "INSERT INTO chat SET (chatid, message, sender, reciever, time) VALUES (?,?,?,?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1, chat.getChatId());
                ps.setString(2, chat.getMessage());
                ps.setString(3, chat.getSender());
                ps.setString(4, chat.getReciever());
                ps.setTime(5, chat.getTime());

                int rows = ps.executeUpdate();
                if(rows > 0){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Chat> getChat(int chatId) {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chat WHERE chatid = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1, chatId);

                try(ResultSet rs = ps.executeQuery()){
                    Chat chat = new Chat();
                    while(rs.next()){
                        chat.setChatId(rs.getInt("chatid"));
                        chat.setMessage(rs.getString("message"));
                        chat.setSender(rs.getString("sender"));
                        chat.setReciever(rs.getString("reciever"));
                        chat.setTime(rs.getTime("time"));

                        chats.add(chat);
                    }

                    return chats;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean editChat(Chat chat) {
        String sql = "UPDATE chat message = ?, sender = ?, reciever = ? VALUES WHERE time = ? AND chatid = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, chat.getMessage());
                ps.setString(2, chat.getSender());
                ps.setString(3, chat.getReciever());
                ps.setTime(4, chat.getTime());
                ps.setInt(5, chat.getChatId());

                int rows = ps.executeUpdate();
                if(rows > 0){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteChat(Chat chat) {
        String sql = "DELETE chat WHERE Time = ? AND chatid = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setTime(1, chat.getTime());
                ps.setInt(2, chat.getChatId());

                int rows = ps.executeUpdate();
                if(rows > 0){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
}
