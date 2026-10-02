package com.portfolio.DAO;

import com.portfolio.model.Users;

public interface UserDAO {
    public boolean addUser(Users user);
    public Users getUser(String username, String password);
    public void updateUsername(Users user, String newUsername, String password);
    public void updatePassword(Users user, String oldPassword, String newPassword);
    public void updateEmail(Users user, String newEmail, String password);
}
