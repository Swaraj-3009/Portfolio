package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.portfolio.DAO.MyProfileDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.MyProfile;

public class MyProfileDaoImpl implements MyProfileDAO{

    @Override
    public MyProfile getProfile() {
        String sql = "SELECT * FROM my_profile WHERE id = 1";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        MyProfile profile = new MyProfile();
                        
                        profile.setName(rs.getString("name"));
                        profile.setAboutMe(rs.getString("about_me"));
                        profile.setEmail(rs.getString("email"));
                        profile.setGithubURL(rs.getString("github_url"));
                        profile.setLinkedinURL(rs.getString("linkedin_url"));
                        profile.setAddress(rs.getString("address"));
                        profile.setPhone(rs.getString("phone"));
                        profile.setProfileImage(rs.getString("profile_image"));

                        return profile;
                    }
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateProfile(MyProfile profile) {
        String sql = "UPDATE my_profile SET name = ? , about_me = ?, email = ?, github_url = ?, linkedin_url = ?, address = ?, phone = ?, profile_image = ? WHERE id = 1";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, profile.getName());
                ps.setString(2, profile.getEmail());
                ps.setString(3, profile.getGithubURL());
                ps.setString(4, profile.getLinkedinURL());
                ps.setString(5, profile.getPhone());
                ps.setString(6, profile.getAddress());
                ps.setString(7, profile.getProfileImage());

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