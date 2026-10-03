package com.portfolio.DAO;

import com.portfolio.model.Users;

public interface UserDAO {
    public boolean addUser(Users user);
    public Users getUser(String username, String password);
    public boolean updateUsername(Users user, String newUsername, String password);
    public boolean updatePassword(Users user, String oldPassword, String newPassword);
    public boolean updateEmail(Users user, String newEmail, String password);
}
