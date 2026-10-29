package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
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
            try(PreparedStatement remove = con.prepareStatement(removeUser)){
                remove.setString(1, username);
                if(remove.executeUpdate() != 1){
                    con.rollback();
                    return false;
                }
            }
            try(PreparedStatement remove = con.prepareStatement(removeMessages)){
                remove.setString(1, username);
                remove.setString(2, username);
                remove.executeUpdate();
            }
            try(PreparedStatement remove = con.prepareStatement(removeConversations)){
                remove.setString(1, username);
                remove.setString(2, username);
                remove.executeUpdate();
            }
            con.commit();
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
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
        String sql = "UPDATE users SET username = ? WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, newUsername );
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
