package com.portfolio.DAO;

import com.portfolio.model.MySkills;

public interface SkillsDAO {
    public void addSkills(MySkills skills);
    public MySkills getSkills();
    public void updateSkills(MySkills skills);
    public void deleteSkills(MySkills skills);
}
