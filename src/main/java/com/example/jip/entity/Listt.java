package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
    @Entity
    @Table(name = "List")
    @Setter
    @Getter
    public class Listt {

        @EmbeddedId
        private ListId id;

        @ManyToOne
        @MapsId("class_id")
        @JoinColumn(name = "class_id")
        private Class clas;

        @ManyToOne
        @MapsId("student_id")
        @JoinColumn(name = "student_id")
        private Student student;

        public Listt(){}

    }
