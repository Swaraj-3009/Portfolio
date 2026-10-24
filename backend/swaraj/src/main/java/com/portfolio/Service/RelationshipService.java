package com.portfolio.Service;

import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.DaoImpl.RelationshipDaoImpl;
import com.portfolio.model.Users;

public class RelationshipService {
    public String relationshipRole(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return new RelationshipDaoImpl().getRole(username.trim());
    }
    private int countOf(List<Users> users) {
        return users == null ? 0 : users.size();
    }



    //followers
    public List<Users> followers() {
        List<Users> list1 =  new RelationshipDaoImpl().getFollowers();
        List<Users> list2 =  new RelationshipDaoImpl().getFriends();
        List<Users> list3 =  new RelationshipDaoImpl().getFamily();

        List<Users> mergedList = new ArrayList<>(list1);
        mergedList.addAll(list2);
        mergedList.addAll(list3);

        return mergedList;
    }
    public int followersCount() {
        return countOf(followers());
    }



    //Friends
    public boolean requestFriends(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().requestFriends(user);
    }
    public boolean cancelFriendRequest(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().cancelFriendRequest(user);
    }
    public List<Users> requestedFriends() {
        return new RelationshipDaoImpl().getRequestedFriends();
    }
    public boolean addFriends(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().updateToFriends(user);
    }
    public List<Users> friends() {
        return new RelationshipDaoImpl().getFriends();
    }
    public int friendsCount() {
        return countOf(friends()) + countOf(family());
    }
    public boolean demoteFriend(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().demoteFriend(user);
    }



    //Family
    public boolean addFamily(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().updateToFamily(user);
    }
    public List<Users> family() {
        return new RelationshipDaoImpl().getFamily();
    }
    public int familyCount() {
        return countOf(family());
    }
    public boolean demoteFamily(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().demoteFamily(user);
    }
}
