package com.example.jip.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListId implements Serializable {
    private int class_id;
    private int student_id;

    public ListId() {}

    public ListId(int class_id, int student_id) {
        this.class_id = class_id;
        this.student_id = student_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListId listId = (ListId) o;
        return class_id == listId.class_id && student_id == listId.student_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(class_id,student_id);
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }
}
