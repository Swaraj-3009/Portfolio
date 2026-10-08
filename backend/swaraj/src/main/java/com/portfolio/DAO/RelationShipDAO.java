package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.Users;

public interface RelationshipDAO {
    public List<Users> getFollowers();
    public boolean requestFriends(Users user);
    public List<Users> getRequestedFriends();
    public boolean updateToFriends(Users user);
    public List<Users> getFriends();
    public boolean updateToFamily(Users user);
    public List<Users> getFamily();
}
