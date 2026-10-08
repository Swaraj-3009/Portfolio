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
    public List<Users> getFollowers() {
        String sql = "SELECT * FROM users WHERE role = followers";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("username"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override 
    public boolean requestFriends(Users user){
        String sql = "UPDATE users SET role = requestedFriends WHERE username = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, user.getUsername());

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
        String sql = "SELECT * FROM users WHERE role = requestedfriends";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("username"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean updateToFriends(Users user) {
        String sql = "UPDATE users SET role = friends WHERE username = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, user.getUsername());

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
        String sql = "SELECT * FROM users WHERE role = friends";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("username"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean updateToFamily(Users user) {
        String sql = "UPDATE users SET role = family WHERE username = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, user.getUsername());

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
        String sql = "SELECT * FROM users WHERE role = family";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                List<Users> u = new ArrayList<>();

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        Users user = new Users();

                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("username"));

                        u.add(user);
                    }
                    return u;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
