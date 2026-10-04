package com.portfolio.DAO;

import com.portfolio.model.MyEducation;

public interface EducationDAO {
    public boolean addEducation(MyEducation education);
    public MyEducation getEducation(int id);
    public boolean updateEducation(MyEducation education);
    public boolean deleteEducation(MyEducation education);
}
