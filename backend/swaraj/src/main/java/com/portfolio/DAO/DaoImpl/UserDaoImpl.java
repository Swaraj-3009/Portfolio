package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.portfolio.DAO.UserDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Users;

public class UserDaoImpl implements UserDAO {

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
                Users user = new Users();

                if(rs.next()){
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                }
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateUsername(Users user, String newUsername, String password) {
        String sql = "UPDATE users SET username = ? WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, newUsername );
                ps.setString(2, user.getUsername());
                ps.setString(3, password);

                int rows = ps.executeUpdate();
                if(rows > 0){
                    System.out.println("Username Updated Succesfully");
                }
                else{
                    System.out.println("Username Not Updated");
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updatePassword(Users user, String oldPassword, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, newPassword );
                ps.setString(2, user.getUsername());
                ps.setString(3, oldPassword);

                int rows = ps.executeUpdate();
                if(rows > 0){
                    System.out.println("Password Updated Succesfully");
                }
                else{
                    System.out.println("Password Not Updated");
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateEmail(Users user, String newEmail, String password) {
        String sql = "UPDATE users SET email = ? WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, newEmail);
                ps.setString(2, user.getUsername());
                ps.setString(3, password);

                int rows = ps.executeUpdate();
                if(rows > 0){
                    System.out.println("Email Updated Succesfully");
                }
                else{
                    System.out.println("Email Not Updated");
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
