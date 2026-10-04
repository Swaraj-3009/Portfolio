package com.portfolio.Service;

import com.portfolio.DAO.AdminDAO;
import com.portfolio.DAO.EducationDAO;
import com.portfolio.DAO.MyProfileDAO;
import com.portfolio.DAO.ProjectsDAO;
import com.portfolio.DAO.SkillsDAO;
import com.portfolio.DAO.DaoImpl.AdminDaoImpl;
import com.portfolio.DAO.DaoImpl.EducationDaoImpl;
import com.portfolio.DAO.DaoImpl.MyProfileDaoImpl;
import com.portfolio.DAO.DaoImpl.ProjectsDaoImpl;
import com.portfolio.DAO.DaoImpl.SkillsDaoImpl;
import com.portfolio.Exception.AdminNotFoundException;
import com.portfolio.model.Admin;
import com.portfolio.model.MyEducation;
import com.portfolio.model.MyProfile;
import com.portfolio.model.MyProject;
import com.portfolio.model.MySkills;

public class AdminService {
    ///verify
    public boolean isLoggedInAdminExist(Admin loggedInAdmin){
        return !(loggedInAdmin == null || loggedInAdmin.getUsername() == null || loggedInAdmin.getUsername().trim().isEmpty());
    }



    //login
    public Admin LoginAdmin(Admin admin) {
        if (admin == null || admin.getUsername() == null || admin.getPassword() == null) {
            throw new AdminNotFoundException();
        }

        AdminDAO adminDao = new AdminDaoImpl();
        Admin fetchAdmin = adminDao.getAdmin(admin.getUsername(), admin.getPassword());

        if(fetchAdmin == null){
            throw new AdminNotFoundException();
        }
        else{
            return fetchAdmin;
        }
    }



    //profile
    public boolean UpdateAdminProfile(Admin loggedInAdmin, MyProfile profile){
        MyProfileDAO myProfileDao = new MyProfileDaoImpl();
        MyProfile myProfile = new MyProfile();

        if (profile == null) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
                if(profile.getName() != null){
                    myProfile.setName(profile.getName());
                }
                if(profile.getEmail() != null){
                    myProfile.setEmail(profile.getEmail());
                }
                if(profile.getGithubURL() != null){
                    myProfile.setGithubURL(profile.getGithubURL());
                }
                if(profile.getLinkedinURL() != null){
                    myProfile.setLinkedinURL(profile.getLinkedinURL());
                }
                if(profile.getPhone() != null){
                    myProfile.setPhone(profile.getPhone());
                }
                if(profile.getAddress() != null){
                    myProfile.setAddress(profile.getAddress());
                }
                if(profile.getAboutMe() != null){
                    myProfile.setAboutMe(profile.getAboutMe());
                }
                if(profile.getProfileImage() != null){
                    myProfile.setProfileImage(profile.getProfileImage());
                }
                if (myProfile.getName() != null || myProfile.getEmail() != null || myProfile.getGithubURL () != null || myProfile.getLinkedinURL() != null || myProfile.getPhone() != null || myProfile.getAddress() != null || myProfile.getAboutMe() != null || myProfile.getProfileImage() != null) {

                    return myProfileDao.updateProfile(myProfile);
                }
        }
        return false;
    }



    //Skill
    public boolean addMySkill(Admin loggedInAdmin, MySkills skill){
        SkillsDAO skillsDao = new SkillsDaoImpl();

        if (skill == null) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
            return skillsDao.addSkills(skill);
        }
        return false;
    }
    public boolean updateMySkill(Admin loggedInAdmin, MySkills skill){
        SkillsDAO skillsDao = new SkillsDaoImpl();

        if (skill == null || skill.getId() <= 0) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
            return skillsDao.updateSkills(skill);
        }
        
        return false;
    }
    public boolean deleteMySkill(Admin loggedInAdmin, MySkills skill){
        SkillsDAO skillsDao = new SkillsDaoImpl();

        if (skill == null || skill.getId() <= 0) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
            return skillsDao.deleteSkills(skill);
        }
        
        return false;
    }



    //Education
    public boolean addMyEducation(Admin loggedInAdmin, MyEducation education){
        EducationDAO educationDao = new EducationDaoImpl();

        if (education == null) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
            return educationDao.addEducation(education);
        }
        return false;
    }
    public boolean updateMyEducation(Admin loggedInAdmin, MyEducation education){
        EducationDAO educationDao = new EducationDaoImpl();
        MyEducation myEducation = new MyEducation();
        myEducation.setId(education.getId());

        if (education == null || education.getId() <= 0) {
            return false;
        }
        if(isLoggedInAdminExist(loggedInAdmin)) {
            if (education.getDegree() != null) {
            myEducation.setDegree(education.getDegree());
            }
            if (education.getInstitution() != null) {
                myEducation.setInstitution(education.getInstitution());
            }
            if (education.getYearofPassing() != null) {
                myEducation.setYearofPassing(education.getYearofPassing());
            }
            if (education.getGrade() != null) {
                myEducation.setGrade(education.getGrade());
            }
            if (education.getDescription() != null) {
                myEducation.setDescription(education.getDescription());
            }

            if (myEducation.getDegree() != null || myEducation.getInstitution() != null || myEducation.getYearofPassing() != null || myEducation.getGrade() != null || myEducation.getDescription() != null) {
                return educationDao.updateEducation(myEducation);
            }
        }
        return false;
    }
    public boolean deleteMyEducation(Admin loggedInAdmin, MyEducation education){
        EducationDAO educationDao = new EducationDaoImpl();

        if (education == null || education.getId() <= 0) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
            return educationDao.deleteEducation(education);
        }
        return false;
    }



    //project
    public boolean addProject(Admin loggedInAdmin, MyProject project){
        ProjectsDAO projectDao = new ProjectsDaoImpl(); 
        
        if (project == null) {
            return false;
        }
        if(isLoggedInAdminExist(loggedInAdmin)){
            return projectDao.addProject(project);
        }
        return false;
    }
    public boolean updateProject(Admin loggedInAdmin, MyProject project){
        ProjectsDAO projectDao = new ProjectsDaoImpl();
        MyProject myProject = new MyProject();
        myProject.setId(project.getId());

        if (project == null || project.getId() <= 0) {
            return false;
        }
        if (isLoggedInAdminExist(loggedInAdmin)) {
            if (project.getProjectName() != null) {
                myProject.setProjectName(project.getProjectName());
            }
            if (project.getProjectDescription() != null) {
                myProject.setProjectDescription(project.getProjectDescription());
            }
            if (project.getTechnologiesUsed() != null) {
                myProject.setTechnologiesUsed(project.getTechnologiesUsed());
            }
            if (project.getGithubURL() != null) {
                myProject.setGithubURL(project.getGithubURL());
            }
            if (project.getLiveURL() != null) {
                myProject.setLiveURL(project.getLiveURL());
            }
            if (project.getProjectImage() != null) {
                myProject.setProjectImage(project.getProjectImage());
            }
            if (project.getIsCompleted() != null) {
                myProject.setIsCompleted(project.getIsCompleted());
            }

            if (myProject.getProjectName() != null || myProject.getProjectDescription() != null || myProject.getTechnologiesUsed() != null || myProject.getGithubURL() != null|| myProject.getLiveURL() != null || myProject.getProjectImage() != null || myProject.getIsCompleted() != null) {
                return projectDao.updateProject(myProject);
            }
        }
        return false;
    }
    public boolean deleteProject(Admin loggedInAdmin, MyProject project){
        ProjectsDAO projectDao = new ProjectsDaoImpl(); 
        
        if (project == null || project.getId() <= 0) {
            return false;
        }
        if(isLoggedInAdminExist(loggedInAdmin)){
            return projectDao.deleteProject(project);
        }
        return false;
    }
}
