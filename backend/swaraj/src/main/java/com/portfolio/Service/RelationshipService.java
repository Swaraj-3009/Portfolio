package com.portfolio.Service;

import java.util.List;

import com.portfolio.DAO.RelationshipDAO;
import com.portfolio.DAO.UserDAO;
import com.portfolio.DAO.DaoImpl.RelationshipDaoImpl;
import com.portfolio.DAO.DaoImpl.UserDaoImpl;
import com.portfolio.model.Users;

public class RelationshipService {
    //Followers
    public List<Users> followers(){
        RelationshipDAO relationDao = new RelationshipDaoImpl();
        return relationDao.getFollowers();
    }
    public int followersCount(){
        RelationshipDAO relationDao = new RelationshipDaoImpl();
        return relationDao.getFollowers().size();
    }


    //Friends
    public boolean addFriends(Users user){
        UserDAO userDao = new UserDaoImpl();
        if(userDao.getUser(user.getUsername(), user.getPassword()) != null){
            RelationshipDAO relationDao = new RelationshipDaoImpl();
            if(relationDao.updateToFriends(user)){
                return true;
            }
        }
        return false;
    }
    public List<Users> friends(){
        RelationshipDAO relationDao = new RelationshipDaoImpl();
        return relationDao.getFriends();
    }
    public int friendsCount(){
        RelationshipDAO relationDao = new RelationshipDaoImpl();
        return relationDao.getFriends().size();
    }


    //Family
    public boolean addFamily(Users user){
        UserDAO userDao = new UserDaoImpl();
        if(userDao.getUser(user.getUsername(), user.getPassword()) != null){
            RelationshipDAO relationDao = new RelationshipDaoImpl();
            if(relationDao.updateToFamily(user)){
                return true;
            }
        }
        return false;
    }
    public List<Users> family(){
        RelationshipDAO relationDao = new RelationshipDaoImpl();
        return relationDao.getFamily();
    }
    public int familyCount(){
        RelationshipDAO relationDao = new RelationshipDaoImpl();
        return relationDao.getFamily().size();
    }
}
