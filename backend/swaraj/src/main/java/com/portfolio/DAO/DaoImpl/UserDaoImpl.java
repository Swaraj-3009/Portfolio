package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.UserDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Users;

public class UserDaoImpl implements UserDAO {

    @Override
    public List<Users> getAllUsers() {
        String sql = "SELECT username, email, role FROM users ORDER BY username";
        List<Users> users = new ArrayList<>();
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Users user = new Users();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean deleteUser(String username) {
        String removeMessages = "DELETE c FROM chat c INNER JOIN chat_user cu "
                + "ON c.conversation_id = cu.conversation_id WHERE cu.user1 = ? OR cu.user2 = ?";
        String removeConversations = "DELETE FROM chat_user WHERE user1 = ? OR user2 = ?";
        String removeUser = "DELETE FROM users WHERE username = ?";
        try(Connection con = DatabaseConnection.getConnection()){
            con.setAutoCommit(false);
            try {
                try(PreparedStatement exists = con.prepareStatement("SELECT 1 FROM users WHERE username = ?")){
                    exists.setString(1, username);
                    try(ResultSet rs = exists.executeQuery()){
                        if(!rs.next()){
                            con.rollback();
                            return false;
                        }
                    }
                }
                // Older databases may not have initialized chat tables yet.
                boolean hasChat = tableExists(con, "chat");
                boolean hasConversations = tableExists(con, "chat_user");
                if(hasChat && hasConversations){
                    try(PreparedStatement remove = con.prepareStatement(removeMessages)){
                        remove.setString(1, username);
                        remove.setString(2, username);
                        remove.executeUpdate();
                    }
                }
                if(hasChat){
                    try(PreparedStatement remove = con.prepareStatement(
                            "DELETE FROM chat WHERE sender = ? OR reciever = ?")){
                        remove.setString(1, username);
                        remove.setString(2, username);
                        remove.executeUpdate();
                    }
                }
                if(hasConversations){
                    try(PreparedStatement remove = con.prepareStatement(removeConversations)){
                        remove.setString(1, username);
                        remove.setString(2, username);
                        remove.executeUpdate();
                    }
                }
                try(PreparedStatement remove = con.prepareStatement(removeUser)){
                    remove.setString(1, username);
                    if(remove.executeUpdate() != 1){
                        con.rollback();
                        return false;
                    }
                }
                con.commit();
                return true;
            } catch(Exception e) {
                try { con.rollback(); } catch(Exception rollbackError) { e.addSuppressed(rollbackError); }
                throw e;
            }
        }
        catch(Exception e){
            throw new IllegalStateException("Could not remove user account and related data", e);
        }
    }

    private boolean tableExists(Connection con, String tableName) throws Exception {
        DatabaseMetaData metadata = con.getMetaData();
        try(ResultSet tables = metadata.getTables(con.getCatalog(), null, tableName, new String[] {"TABLE"})){
            return tables.next();
        }
    }

    @Override
    public boolean addUser(Users user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            int rows = ps.executeUpdate();
            if(rows > 0){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Users getUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Users user = new Users();
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;

                }
                else{
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUsername(Users user, String newUsername, String password) {
        String updateUser = "UPDATE users SET username = ? WHERE username = ? AND password = ?";
        String updateConversations = "UPDATE chat_user SET user1 = ? WHERE user1 = ?";
        String updateMessages = "UPDATE chat SET sender = ? WHERE sender = ?";
        String updateRecipients = "UPDATE chat SET reciever = ? WHERE reciever = ?";

        try(Connection con = DatabaseConnection.getConnection()){
            con.setAutoCommit(false);
            try{
                try(PreparedStatement ps = con.prepareStatement(updateUser)){
                    ps.setString(1, newUsername);
                    ps.setString(2, user.getUsername());
                    ps.setString(3, password);
                    if(ps.executeUpdate() == 0){
                        con.rollback();
                        return false;
                    }
                }
                try(PreparedStatement ps = con.prepareStatement(updateConversations)){
                    ps.setString(1, newUsername);
                    ps.setString(2, user.getUsername());
                    ps.executeUpdate();
                }
                try(PreparedStatement ps = con.prepareStatement("UPDATE chat_user SET user2 = ? WHERE user2 = ?")){
                    ps.setString(1, newUsername);
                    ps.setString(2, user.getUsername());
                    ps.executeUpdate();
                }
                canonicalizeRenamedConversations(con, newUsername);
                try(PreparedStatement ps = con.prepareStatement(updateMessages)){
                    ps.setString(1, newUsername);
                    ps.setString(2, user.getUsername());
                    ps.executeUpdate();
                }
                try(PreparedStatement ps = con.prepareStatement(updateRecipients)){
                    ps.setString(1, newUsername);
                    ps.setString(2, user.getUsername());
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch(Exception e){
                con.rollback();
                throw e;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void canonicalizeRenamedConversations(Connection con, String username) throws Exception {
        String select = "SELECT conversation_id, LEAST(user1, user2) AS first_user, "
                + "GREATEST(user1, user2) AS second_user FROM chat_user WHERE user1 = ? OR user2 = ?";
        String update = "UPDATE chat_user SET user1 = ?, user2 = ? WHERE conversation_id = ?";
        try(PreparedStatement query = con.prepareStatement(select)){
            query.setString(1, username);
            query.setString(2, username);
            try(ResultSet rs = query.executeQuery();
                PreparedStatement statement = con.prepareStatement(update)){
                while(rs.next()){
                    String user1 = rs.getString("first_user");
                    String user2 = rs.getString("second_user");
                    int conversationId = rs.getInt("conversation_id");
                    statement.setString(1, user1);
                    statement.setString(2, user2);
                    statement.setInt(3, conversationId);
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
    }

    @Override
    public boolean updatePassword(Users user, String oldPassword, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, newPassword );
                ps.setString(2, user.getUsername());
                ps.setString(3, oldPassword);

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
    public boolean updateEmail(Users user, String newEmail, String password) {
        String sql = "UPDATE users SET email = ? WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, newEmail);
                ps.setString(2, user.getUsername());
                ps.setString(3, password);

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
