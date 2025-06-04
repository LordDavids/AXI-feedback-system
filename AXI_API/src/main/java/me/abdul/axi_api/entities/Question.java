package me.abdul.axi_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "question")
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @NotNull
    @Column(name = "public", nullable = false)
    private Boolean isPublic;
}
