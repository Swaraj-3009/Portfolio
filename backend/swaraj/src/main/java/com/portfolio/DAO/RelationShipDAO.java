package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.Users;

public interface RelationshipDAO {
    public String getRole(String username);
    public List<Users> getFollowers();
    public boolean requestFriends(Users user);
    public List<Users> getRequestedFriends();
    public boolean updateToFriends(Users user);
    public List<Users> getFriends();
    public boolean updateToFamily(Users user);
    public List<Users> getFamily();
    public boolean demoteFamily(Users user);
    public boolean demoteFriend(Users user);
    public boolean cancelFriendRequest(Users user);
}
