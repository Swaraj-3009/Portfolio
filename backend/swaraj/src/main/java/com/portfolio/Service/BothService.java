package com.portfolio.Service;

import java.util.List;

import com.portfolio.DAO.EducationDAO;
import com.portfolio.DAO.MyProfileDAO;
import com.portfolio.DAO.ProjectsDAO;
import com.portfolio.DAO.SkillsDAO;
import com.portfolio.DAO.DaoImpl.EducationDaoImpl;
import com.portfolio.DAO.DaoImpl.MyProfileDaoImpl;
import com.portfolio.DAO.DaoImpl.ProjectsDaoImpl;
import com.portfolio.DAO.DaoImpl.SkillsDaoImpl;
import com.portfolio.model.MyEducation;
import com.portfolio.model.MyProfile;
import com.portfolio.model.MyProject;
import com.portfolio.model.MySkills;

public class BothService {

    public MyProfile showProfile() {
        MyProfileDAO myProfileDao = new MyProfileDaoImpl();
        return myProfileDao.getProfile();
    }

    public List<MySkills> showMySkill() {
        SkillsDAO mySkillDao = new SkillsDaoImpl();
        return mySkillDao.getSkills();
    }

    public List<MyEducation> showMyEducation() {
        EducationDAO myEducation = new EducationDaoImpl();
        return myEducation.getEducation();
    }
    
    public List<MyProject> showMyProject(){
        ProjectsDAO myProject = new ProjectsDaoImpl();
        return myProject.getProject();
    }
}
