package com.portfolio.swaraj.Model;

public class Myproject {
    private String projectName;
    private String projectDescription;
    private String TechnologiesUsed;
    private String GithubURL;
    private String LiveURL;
    private String projectImage;
    private Boolean isCompleted;

    //constructor
    public Myproject(String projectName, String projectDescription, String TechnologiesUsed, String GithubURL, String LiveURL, String projectImage, Boolean isCompleted) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.TechnologiesUsed = TechnologiesUsed;
        this.GithubURL = GithubURL;
        this.LiveURL = LiveURL;
        this.projectImage = projectImage;
        this.isCompleted = isCompleted;
    }

    //getters
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
