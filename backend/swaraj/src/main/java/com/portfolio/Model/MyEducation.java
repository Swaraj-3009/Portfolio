package com.portfolio.model;

public class MyEducation {
    private int id;
    private String degree;
    private String institution;
    private String yearofPassing;
    private String grade;
    private String description;

    //getters
    public int getId(){
        return id;
    }
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
    public void setId(int id){
        this.id = id;
    }
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
