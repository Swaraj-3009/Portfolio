package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.ChatDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Chat;

public class ChatDaoImpl implements ChatDAO{

    @Override
    public boolean sendChat(Chat chat) {
        String sql = "INSERT INTO chat (conversation_id, message, sender, reciever, time) VALUES (?, ?, ?, ?, ?)";

        try(Connection con = DatabaseConnection.getConnection()){
                boolean autoCommit = con.getAutoCommit();
                con.setAutoCommit(false);
                try {
                    int conversationId = getOrCreateConversationId(con, chat.getSender(), chat.getReciever());
                    chat.setConversationId(conversationId);
                    try(PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                        ps.setInt(1, conversationId);
                        ps.setString(2, chat.getMessage());
                        ps.setString(3, chat.getSender());
                        ps.setString(4, chat.getReciever());
                        ps.setObject(5, chat.getTime());

                        int rows = ps.executeUpdate();
                        if(rows > 0){
                            try(ResultSet keys = ps.getGeneratedKeys()){
                                if(keys.next()){
                                    chat.setMessageId(keys.getLong(1));
                                }
                            }
                            con.commit();
                            return true;
                        }
                    }
                    con.rollback();
                } catch(Exception e) {
                    con.rollback();
                    throw e;
                } finally {
                    con.setAutoCommit(autoCommit);
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Chat> getChat(int conversationId) {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chat WHERE conversation_id = ? ORDER BY time, message_id";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1, conversationId);

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Chat chat = new Chat();
                        
                        chat.setMessageId(rs.getLong("message_id"));
                        chat.setConversationId(rs.getInt("conversation_id"));
                        chat.setMessage(rs.getString("message"));
                        chat.setSender(rs.getString("sender"));
                        chat.setReciever(rs.getString("reciever"));
                        chat.setTime(rs.getObject("time", LocalDateTime.class));

                        chats.add(chat);
                    }

                    return chats;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return chats;
    }

    @Override
    public boolean editChat(Chat chat) {
        String sql = "UPDATE chat SET message = ? WHERE message_id = ? AND conversation_id = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, chat.getMessage());
                ps.setLong(2, chat.getMessageId());
                ps.setInt(3, chat.getConversationId());

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
        String sql = "DELETE FROM chat WHERE message_id = ? AND conversation_id = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setLong(1, chat.getMessageId());
                ps.setInt(2, chat.getConversationId());

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
    
    private int getOrCreateConversationId(Connection con, String sender, String reciever) throws Exception {
        String insertSql = "INSERT INTO chat_user (user1, user2) "
                + "VALUES (LEAST(?, ?), GREATEST(?, ?)) "
                + "ON DUPLICATE KEY UPDATE conversation_id = conversation_id";
        try(PreparedStatement ps = con.prepareStatement(insertSql)){
            ps.setString(1, sender);
            ps.setString(2, reciever);
            ps.setString(3, sender);
            ps.setString(4, reciever);
            ps.executeUpdate();
        }

        String selectSql = "SELECT conversation_id FROM chat_user "
                + "WHERE user1 = LEAST(?, ?) AND user2 = GREATEST(?, ?)";
        try(PreparedStatement ps = con.prepareStatement(selectSql)){
            ps.setString(1, sender);
            ps.setString(2, reciever);
            ps.setString(3, sender);
            ps.setString(4, reciever);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("conversation_id");
                }
            }
        }
        throw new IllegalStateException("Conversation could not be created or found");
    }
}
