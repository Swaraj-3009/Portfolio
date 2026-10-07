package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.Users;

public interface RelationShipDAO {
    public List<Users> getFollowers();
    public boolean updateToFriends(Users user);
    public List<Users> getFriends();
    public boolean updateToFamily(Users user);
    public List<Users> getFamily();
}
