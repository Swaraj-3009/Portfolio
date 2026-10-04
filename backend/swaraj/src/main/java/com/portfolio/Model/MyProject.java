package com.portfolio.model;

public class MyProject {
    private int id;
    private String projectName;
    private String projectDescription;
    private String TechnologiesUsed;
    private String GithubURL;
    private String LiveURL;
    private String projectImage;
    private Boolean isCompleted;

    //getters
    public int getId(){
        return id;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getProjectDescription() {
        return projectDescription;
    }
    public String getTechnologiesUsed() {
        return TechnologiesUsed;
    }
    public String getGithubURL() {
        return GithubURL;
    }
    public String getLiveURL() {
        return LiveURL;
    }
    public String getProjectImage() {
        return projectImage;
    }
    public Boolean getIsCompleted() {
        return isCompleted;
    }


    //setters
    public void setId(int id){
        this.id = id;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
    public void setTechnologiesUsed(String technologiesUsed) {
        TechnologiesUsed = technologiesUsed;
    }
    public void setGithubURL(String githubURL) {
        GithubURL = githubURL;
    }
    public void setLiveURL(String liveURL) {
        LiveURL = liveURL;
    }
    public void setProjectImage(String projectImage) {
        this.projectImage = projectImage;
    }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
