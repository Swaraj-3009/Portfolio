package com.portfolio.Service;

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

    public List<Users> followers() {
        return new RelationshipDaoImpl().getFollowers();
    }

    public int followersCount() {
        List<Users> followers = followers();
        return followers == null ? 0 : followers.size();
    }

    public boolean requestFriends(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().requestFriends(user);
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
        List<Users> friends = friends();
        return friends == null ? 0 : friends.size();
    }

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
        List<Users> family = family();
        return family == null ? 0 : family.size();
    }

    public boolean demoteFamily(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().demoteFamily(user);
    }

    public boolean demoteFriend(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().demoteFriend(user);
    }

    public boolean cancelFriendRequest(Users user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return new RelationshipDaoImpl().cancelFriendRequest(user);
    }
}
