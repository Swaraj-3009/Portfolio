package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.MySkills;

public interface SkillsDAO {
    public boolean addSkills(MySkills skills);
    public List<MySkills> getSkills();
    public boolean updateSkills(MySkills skills);
    public boolean deleteSkills(MySkills skills);
}
