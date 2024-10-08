package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Material {
    private int id;
    private String content;
    private String img;
    private int teacher_id;

    public Material() {}

    public Material(int id, String content, String img, int teacher_id) {
        this.id = id;
        this.content = content;
        this.img = img;
        this.teacher_id = teacher_id;
    }
}
