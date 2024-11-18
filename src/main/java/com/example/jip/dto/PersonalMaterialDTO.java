package com.example.jip.dto;

public class PersonalMaterialDTO {
    private int id;
    private String material_link;
    private StudentDTO student;
    private MaterialDTO material; // Chứa đối tượng MaterialDTO

    public PersonalMaterialDTO() {
    }

    public PersonalMaterialDTO(int id, String material_link, StudentDTO student, MaterialDTO material) {
        this.id = id;
        this.material_link = material_link;
        this.student = student;
        this.material = material;
    }

    public MaterialDTO getMaterial() {
        return material;
    }

    public void setMaterial(MaterialDTO material) {
        this.material = material;
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
