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
@Table(name = "form")
@Entity
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @ElementCollection
    @CollectionTable(name = "form_team", joinColumns = @JoinColumn(name = "form_id"))
    @Column(name = "team_id")
    private List<Integer> teamIds = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "form_question",
            joinColumns = @JoinColumn(name = "form_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private FormCategory category;
}
