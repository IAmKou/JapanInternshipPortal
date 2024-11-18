package com.example.jip.dto;

public class PersonalMaterialDTO {
    private int id;
    private String material_link;
    private StudentDTO student;

    public PersonalMaterialDTO() {
    }

    public PersonalMaterialDTO(int id, String material_link, StudentDTO student) {
        this.id = id;
        this.material_link = material_link;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterial_link() {
        return material_link;
    }

    public void setMaterial_link(String material_link) {
        this.material_link = material_link;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }
}
