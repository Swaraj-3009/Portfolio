package com.portfolio.Service;

import com.portfolio.DAO.AdminDAO;
import com.portfolio.DAO.DaoImpl.AdminDaoImpl;
import com.portfolio.Exception.AdminNotFoundException;
import com.portfolio.model.Admin;

public class AdminService {

    public Admin LoginAdmin(Admin admin) {
        AdminDAO adminDao = new AdminDaoImpl();

        Admin fetchAdmin = adminDao.getAdmin(admin.getUsername(), admin.getPassword());

        if(fetchAdmin == null){
            throw new AdminNotFoundException();
        }
        else{
            return admin;
        }
    }
    
}
