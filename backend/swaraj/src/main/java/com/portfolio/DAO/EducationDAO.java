package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.MyEducation;

public interface EducationDAO {
    public boolean addEducation(MyEducation education);
    public List<MyEducation> getEducation();
    public boolean updateEducation(MyEducation education);
    public boolean deleteEducation(MyEducation education);
}
