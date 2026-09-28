package com.portfolio.Model;

public class MySkills {
    private String skillName;
    private Boolean isCompleted;

    //constructor
    public MySkills(String skillName, Boolean isCompleted) {
        this.skillName = skillName;
        this.isCompleted = isCompleted;
    }

    //getters
    public String getSkillName() {
        return skillName;
    }
    public Boolean getIsCompleted() {
        return isCompleted;
    }

    //setters
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    
}
