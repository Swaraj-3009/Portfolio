package com.portfolio.Model;

public class MyEducation {
    private String degree;
    private String institution;
    private String yearofPassing;
    private String grade;
    private String description;

    //constructor
    public MyEducation(String degree, String institution, String yearofPassing, String grade, String description) {
        this.degree = degree;
        this.institution = institution;
        this.yearofPassing = yearofPassing;
        this.grade = grade;
        this.description = description;
    }

    //getters
    public String getDegree() {
        return degree;
    }
    public String getInstitution() {
        return institution;
    }
    public String getYearofPassing() {
        return yearofPassing;
    }
    public String getGrade() {
        return grade;
    }
    public String getDescription() {
        return description;
    }

    //setters
    public void setDegree(String degree) {
        this.degree = degree;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    public void setYearofPassing(String yearofPassing) {
        this.yearofPassing = yearofPassing;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
