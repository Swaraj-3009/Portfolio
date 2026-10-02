package com.portfolio.Service;

import com.portfolio.model.Users;
import com.portfolio.DAO.UserDAO;
import com.portfolio.DAO.DaoImpl.UserDaoImpl;
import com.portfolio.Exception.UserNotFoundException;

public class UserService {
    public boolean registerUser(String username, String password, String email, String role){
        Users user = new Users();
        UserDAO userDao = new UserDaoImpl();
        Users fetchedUser = userDao.getUser(username, password);

        if(fetchedUser != null){
            loginUser(username, password);
        }
        else{
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);

            if(userDao.addUser(user)){
                return true;
            }
        }
        return false;
    }

    public Users loginUser(String username, String password){
        UserDAO userDao = new UserDaoImpl();

        Users user = userDao.getUser(username, password);

        if(user == null){
            throw new UserNotFoundException();
        }
        else{
            return user;
        }
    }

    // public void UpdateUsername(String password){
        
    // }
}
