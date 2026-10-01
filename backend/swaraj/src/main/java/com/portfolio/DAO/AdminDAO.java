package com.portfolio.DAO;

import com.portfolio.model.Admin;

public interface AdminDAO {
    public Admin getAdmin(String username, String password);
}
