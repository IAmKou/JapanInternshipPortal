package com.example.jip.dto;

import com.example.jip.entity.Class;

public class ClassSemesterDTO {
    private int classId;
    private String semesterName;
    private String className;

    public ClassSemesterDTO(int classId, String semesterName, String className) {
        this.classId = classId;
        this.semesterName = semesterName;
        this.className = className;
    }

    public ClassSemesterDTO(Class clasz) {
        this.classId = clasz.getId();
        this.semesterName = clasz.getSemester().getName();
        this.className = clasz.getName();
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
