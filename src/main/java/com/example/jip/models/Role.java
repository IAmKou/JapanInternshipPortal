package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Role {
    private int id;
    private String name;

    public Role() {}

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
