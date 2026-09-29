package com.portfolio.DAO;

import com.portfolio.model.MyProfile;

public interface ProfileDAO {
    public void updateUsename(MyProfile profile);
    public void updatePassword(MyProfile profile);
    public void updateEmail(MyProfile profile);
    public void updateGithubURL(MyProfile profile);
    public void updateLinkedinURL(MyProfile profile);
    public void updatePhone(MyProfile profile);
    public void updateAddress(MyProfile profile);
    public void updateAboutMe(MyProfile profile);
    public void updateProfileImage(MyProfile profile);
}
