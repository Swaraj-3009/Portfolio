package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.portfolio.DAO.AdminDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.Admin;

public class AdminDaoImpl implements AdminDAO {
    
    @Override
    public Admin getAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1,username);
                ps.setString(2, password);

                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        Admin admin = new Admin();

                        admin.setUsername(username);
                        admin.setPassword(password);

                        return admin;
                    }
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
    
}
