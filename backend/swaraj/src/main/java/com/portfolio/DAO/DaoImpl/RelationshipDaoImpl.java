package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.RelationshipDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Users;

public class RelationshipDaoImpl implements RelationshipDAO {

    @Override
    public String getRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? rs.getString("role") : null;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Users> getFollowers() {
        String sql = "SELECT username, email FROM users WHERE role IN (?, ?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "follower");
                ps.setString(2, "followers");
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override 
    public boolean requestFriends(Users user){
        String sql = "UPDATE users SET role = ? WHERE username = ? AND role IN (?, ?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "requestedFriends");
                ps.setString(2, user.getUsername());
                ps.setString(3, "follower");
                ps.setString(4, "followers");

                int rows = ps.executeUpdate();
                if(rows > 0 ){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public List<Users> getRequestedFriends() {
        String sql = "SELECT username, email FROM users WHERE role = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "requestedFriends");
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    @Override
    public boolean updateToFriends(Users user) {
        String sql = "UPDATE users SET role = ? WHERE username = ? AND role = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "friends");
                ps.setString(2, user.getUsername());
                ps.setString(3, "requestedFriends");

                int rows = ps.executeUpdate();
                if(rows > 0){
                    return true;
                }
                else{
                     return false;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Users> getFriends() {
        String sql = "SELECT username, email FROM users WHERE role = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "friends");
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    public boolean updateToFamily(Users user) {
        String sql = "UPDATE users SET role = ? WHERE username = ? AND role = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "family");
                ps.setString(2, user.getUsername());
                ps.setString(3, "friends");

                int rows = ps.executeUpdate();
                if(rows > 0){
                    return true;
                }
                else{
                     return false;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Users> getFamily() {
        String sql = "SELECT username, email FROM users WHERE role = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, "family");
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean demoteFamily(Users user) {
        return updateRole(user, "family", "friends");
    }

    @Override
    public boolean demoteFriend(Users user) {
        return updateRole(user, "friends", "follower");
    }

    @Override
    public boolean cancelFriendRequest(Users user) {
        return updateRole(user, "requestedFriends", "follower");
    }

    private boolean updateRole(Users user, String currentRole, String nextRole) {
        String sql = "UPDATE users SET role = ? WHERE username = ? AND role = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, nextRole);
                ps.setString(2, user.getUsername());
                ps.setString(3, currentRole);
                return ps.executeUpdate() > 0;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
