package com.portfolio.DAO;

import com.portfolio.model.MyProfile;

public interface MyProfileDAO {
    public MyProfile getProfile();
    public boolean updateProfile(MyProfile profile);
}
