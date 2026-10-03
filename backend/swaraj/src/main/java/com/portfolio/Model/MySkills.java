package com.portfolio.model;

public class MySkills {
    private int id;
    private String skillName;
    private Boolean isCompleted;

    //getters
    public int getId(){
        return id;
    }
    public String getSkillName() {
        return skillName;
    }
    public Boolean getIsCompleted() {
        return isCompleted;
    }

    //setters
    public void setId(int id){
        this.id = id;
    }
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    
}
