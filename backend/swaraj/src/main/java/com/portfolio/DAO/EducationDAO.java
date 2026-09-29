package com.portfolio.DAO;

import com.portfolio.model.MyEducation;

public interface EducationDAO {
    public void addEducation(MyEducation education);
    public MyEducation getEducation(String degree);
    public void updateEducation(MyEducation education);
    public void deleteEducation(MyEducation education);
}
