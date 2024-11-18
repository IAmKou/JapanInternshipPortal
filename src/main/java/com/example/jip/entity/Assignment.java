package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name =  "date_created")
    @Temporal(TemporalType.DATE)
    Date created_date;
    @Column(name =  "end_date")
    @Temporal(TemporalType.DATE)
    Date end_date;
    @Column(name = "description")
    String description;
    @Column(name = "content")
    String content;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id", fetch = FetchType.EAGER)
    List<Class> classList = new ArrayList<>();


    @Column(name = "img")
    String imgUrl;



}
