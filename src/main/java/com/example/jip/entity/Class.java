package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

    @Entity
    @Table(name = "Class")
    @Setter
    @Getter
    public class Class {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "number_of_student", nullable = false)
        private int number_of_student;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "teacher_id", nullable = false)
        private Teacher teacher;

        @Column(name="status")
        @Enumerated(EnumType.STRING)
        private status status;

        @OneToMany(mappedBy = "clas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Set<Listt> classLists = new HashSet<>();

        @ManyToMany(mappedBy = "classes")
        private Set<Assignment> assignments = new HashSet<>();

        @OneToMany(mappedBy = "clasz", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private Set<Schedule> schedules = new HashSet<>();

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "semester_id")
        private Semester semester;

        public Class(){}

        public Class(Class.status status, Teacher teacher, int number_of_student, String name, int id) {
            this.status = status;
            this.teacher = teacher;
            this.number_of_student = number_of_student;
            this.name = name;
            this.id = id;
        }

        public enum status{
            Active, Inactive
        }
    }
