package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalMaterial {
    private int id;
    private int student_id;
    private String material_link;

    public PersonalMaterial() {}

    public PersonalMaterial(int id, int student_id, String material_link) {
        this.id = id;
        this.student_id = student_id;
        this.material_link = material_link;
    }
}
