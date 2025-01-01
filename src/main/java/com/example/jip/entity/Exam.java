package com.example.jip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MarkReportExam> markReportExams = new ArrayList<>();

    @Column(name = "title")
    String title;
    @Column(name = "kanji")
    BigDecimal kanji;
    @Column(name = "bunpou")
    BigDecimal bunpou;
    @Column(name = "kotoba")
    BigDecimal kotoba;

}
