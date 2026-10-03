package com.portfolio.Service;

import com.portfolio.DAO.AdminDAO;
import com.portfolio.DAO.MyProfileDAO;
import com.portfolio.DAO.DaoImpl.AdminDaoImpl;
import com.portfolio.DAO.DaoImpl.MyProfileDaoImpl;
import com.portfolio.Exception.AdminNotFoundException;
import com.portfolio.model.Admin;
import com.portfolio.model.MyProfile;

public class AdminService {

    public Admin LoginAdmin(Admin admin) {
        AdminDAO adminDao = new AdminDaoImpl();

        Admin fetchAdmin = adminDao.getAdmin(admin.getUsername(), admin.getPassword());

        if(fetchAdmin == null){
            throw new AdminNotFoundException();
        }
        else{
            return fetchAdmin;
        }
    }

    public boolean UpdateAdminProfile(Admin loggedInAdmin, MyProfile profile){
        MyProfileDAO myProfileDao = new MyProfileDaoImpl();
        MyProfile myProfile = new MyProfile();

        if (!(loggedInAdmin == null || loggedInAdmin.getUsername() == null || profile == null)) {
                if(profile.getName() != null){
                    myProfile.setName(profile.getName());
                }
                if(profile.getEmail() != null){
                    myProfile.setEmail(profile.getEmail());
                }
                if(profile.getGithubURL() != null){
                    myProfile.setGithubURL(profile.getGithubURL());
                }
                if(profile.getLinkedinURL() != null){
                    myProfile.setLinkedinURL(profile.getLinkedinURL());
                }
                if(profile.getPhone() != null){
                    myProfile.setPhone(profile.getPhone());
                }
                if(profile.getAddress() != null){
                    myProfile.setAddress(profile.getAddress());
                }
                if(profile.getAboutMe() != null){
                    myProfile.setAboutMe(profile.getAboutMe());
                }
                if(profile.getProfileImage() != null){
                    myProfile.setProfileImage(profile.getProfileImage());
                }
                if (myProfile.getName() != null || myProfile.getEmail() != null || myProfile.getGithubURL () != null || myProfile.getLinkedinURL() != null || myProfile.getPhone() != null || myProfile.getAddress() != null || myProfile.getAboutMe() != null || myProfile.getProfileImage() != null) {

                    return myProfileDao.updateProfile(myProfile);
                }
        }

        return false;
    }

    public MyProfile ShowProfile(){
        MyProfileDAO myProfileDao = new MyProfileDaoImpl();
        MyProfile myProfile = myProfileDao.getProfile();

        return myProfile;
    }
}
