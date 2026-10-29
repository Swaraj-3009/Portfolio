package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.ChatDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Chat;
import com.portfolio.model.ChatConversation;

public class ChatDaoImpl implements ChatDAO{
    private static volatile boolean chatSettingsInitialized;

    @Override
    public boolean isChatEnabled() {
        String sql = "SELECT enabled FROM chat_settings WHERE setting_id = 1";
        try(Connection con = DatabaseConnection.getConnection()){
            initializeChatSettings(con);
            try(PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
                return rs.next() && rs.getBoolean("enabled");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setChatEnabled(boolean enabled) {
        String sql = "INSERT INTO chat_settings (setting_id, enabled) VALUES (1, ?) "
                + "ON DUPLICATE KEY UPDATE enabled = VALUES(enabled)";
        try(Connection con = DatabaseConnection.getConnection()){
            initializeChatSettings(con);
            try(PreparedStatement ps = con.prepareStatement(sql)){
                ps.setBoolean(1, enabled);
                ps.executeUpdate();
                return true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void initializeChatSettings(Connection con) throws Exception {
        if(chatSettingsInitialized) return;
        synchronized(ChatDaoImpl.class){
            if(chatSettingsInitialized) return;
            ensureChatTables(con);
            ensureChatSettingsTable(con);
            try(PreparedStatement init = con.prepareStatement(
                    "INSERT IGNORE INTO chat_settings (setting_id, enabled) VALUES (1, TRUE)")){
                init.executeUpdate();
            }
            chatSettingsInitialized = true;
        }
    }

    private void ensureChatTables(Connection con) throws Exception {
        String conversationsSql = "CREATE TABLE IF NOT EXISTS chat_user ("
                + "conversation_id INT NOT NULL AUTO_INCREMENT, "
                + "user1 VARCHAR(64) NOT NULL, "
                + "user2 VARCHAR(64) NOT NULL, "
                + "PRIMARY KEY (conversation_id), "
                + "UNIQUE KEY uq_chat_user_pair (user1, user2)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        String messagesSql = "CREATE TABLE IF NOT EXISTS chat ("
                + "message_id BIGINT NOT NULL AUTO_INCREMENT, "
                + "conversation_id INT NOT NULL, "
                + "message TEXT NOT NULL, "
                + "sender VARCHAR(64) NOT NULL, "
                + "reciever VARCHAR(64) NOT NULL, "
                + "time DATETIME(6) NOT NULL, "
                + "PRIMARY KEY (message_id), "
                + "KEY idx_chat_conversation_time (conversation_id, time, message_id), "
                + "CONSTRAINT fk_chat_conversation FOREIGN KEY (conversation_id) "
                + "REFERENCES chat_user (conversation_id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try(Statement statement = con.createStatement()){
            statement.execute(conversationsSql);
            statement.execute(messagesSql);
        }
    }

    private void ensureChatSettingsTable(Connection con) throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS chat_settings ("
                + "setting_id TINYINT NOT NULL, "
                + "enabled BOOLEAN NOT NULL DEFAULT TRUE, "
                + "PRIMARY KEY (setting_id), "
                + "CONSTRAINT chk_chat_settings_singleton CHECK (setting_id = 1)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try(Statement statement = con.createStatement()){
            statement.execute(sql);
        }
    }

    @Override
    public boolean accountExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? "
                + "UNION ALL SELECT 1 FROM admin WHERE username = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isAdminAccount(String username) {
        String sql = "SELECT 1 FROM admin WHERE username = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int createConversation(String user1, String user2) {
        try(Connection con = DatabaseConnection.getConnection()){
            return getOrCreateConversationId(con, user1, user2);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isConversationParticipant(int conversationId, String username) {
        String sql = "SELECT 1 FROM chat_user WHERE conversation_id = ? AND (user1 = ? OR user2 = ?)";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, conversationId);
            ps.setString(2, username);
            ps.setString(3, username);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getConversationPeer(int conversationId, String username) {
        String sql = "SELECT CASE WHEN user1 = ? THEN user2 ELSE user1 END AS peer "
                + "FROM chat_user WHERE conversation_id = ? AND (user1 = ? OR user2 = ?)";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setInt(2, conversationId);
            ps.setString(3, username);
            ps.setString(4, username);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next() ? rs.getString("peer") : null;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatConversation> getAllConversations() {
        List<ChatConversation> conversations = new ArrayList<>();
        String sql = "SELECT conversation_id, user1, user2 FROM chat_user ORDER BY conversation_id DESC";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                ChatConversation conversation = new ChatConversation();
                conversation.setConversationId(rs.getInt("conversation_id"));
                conversation.setUser1(rs.getString("user1"));
                conversation.setUser2(rs.getString("user2"));
                conversations.add(conversation);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return conversations;
    }

    @Override
    public List<ChatConversation> getConversationsForUser(String username) {
        List<ChatConversation> conversations = new ArrayList<>();
        String sql = "SELECT conversation_id, user1, user2 FROM chat_user "
                + "WHERE user1 = ? OR user2 = ? ORDER BY conversation_id DESC";
        try(Connection con = DatabaseConnection.getConnection()){
            initializeChatSettings(con);
            try(PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, username);
                ps.setString(2, username);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        ChatConversation conversation = new ChatConversation();
                        conversation.setConversationId(rs.getInt("conversation_id"));
                        conversation.setUser1(rs.getString("user1"));
                        conversation.setUser2(rs.getString("user2"));
                        conversations.add(conversation);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return conversations;
    }

    @Override
    public boolean sendChat(Chat chat) {
        String sql = "INSERT INTO chat (conversation_id, message, sender, reciever, time) VALUES (?, ?, ?, ?, ?)";

        try(Connection con = DatabaseConnection.getConnection()){
                boolean autoCommit = con.getAutoCommit();
                con.setAutoCommit(false);
                try {
                    if(!conversationMatchesUsers(con, chat.getConversationId(), chat.getSender(), chat.getReciever())){
                        con.rollback();
                        return false;
                    }
                    try(PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                        ps.setInt(1, chat.getConversationId());
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
        String sql = "UPDATE chat SET message = ? WHERE message_id = ? AND conversation_id = ? AND sender = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, chat.getMessage());
                ps.setLong(2, chat.getMessageId());
                ps.setInt(3, chat.getConversationId());
                ps.setString(4, chat.getSender());

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
        String sql = "DELETE FROM chat WHERE message_id = ? AND conversation_id = ? AND sender = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setLong(1, chat.getMessageId());
                ps.setInt(2, chat.getConversationId());
                ps.setString(3, chat.getSender());

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
    public boolean editChatAsAdmin(Chat chat) {
        String sql = "UPDATE chat SET message = ? WHERE message_id = ? AND conversation_id = ?";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, chat.getMessage());
            ps.setLong(2, chat.getMessageId());
            ps.setInt(3, chat.getConversationId());
            return ps.executeUpdate() > 0;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteChatAsAdmin(Chat chat) {
        String sql = "DELETE FROM chat WHERE message_id = ? AND conversation_id = ?";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, chat.getMessageId());
            ps.setInt(2, chat.getConversationId());
            return ps.executeUpdate() > 0;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    private int getOrCreateConversationId(Connection con, String sender, String reciever) throws Exception {
        String first = sender.compareToIgnoreCase(reciever) <= 0 ? sender : reciever;
        String second = first.equals(sender) ? reciever : sender;
        int existingId = findConversationId(con, first, second);
        if (existingId > 0) return existingId;

        String insertSql = "INSERT INTO chat_user (user1, user2) VALUES (?, ?)";
        try(PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, first);
            ps.setString(2, second);
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()) return keys.getInt(1);
            }
            existingId = findConversationId(con, first, second);
            if (existingId > 0) return existingId;
        } catch (SQLException e) {
            // A concurrent request can insert the same unique user pair after our lookup.
            if (!"23000".equals(e.getSQLState())) throw e;
            existingId = findConversationId(con, first, second);
            if (existingId > 0) return existingId;
            throw e;
        }
        throw new IllegalStateException("Conversation row was inserted without a generated ID");
    }

    private int findConversationId(Connection con, String first, String second) throws Exception {
        String sql = "SELECT conversation_id FROM chat_user WHERE user1 = ? AND user2 = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, first);
            ps.setString(2, second);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next() ? rs.getInt("conversation_id") : 0;
            }
        }
    }

    private boolean conversationMatchesUsers(Connection con, int conversationId, String sender, String reciever) throws Exception {
        String sql = "SELECT 1 FROM chat_user WHERE conversation_id = ? "
                + "AND user1 = LEAST(?, ?) AND user2 = GREATEST(?, ?)";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, conversationId);
            ps.setString(2, sender);
            ps.setString(3, reciever);
            ps.setString(4, sender);
            ps.setString(5, reciever);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }
}
