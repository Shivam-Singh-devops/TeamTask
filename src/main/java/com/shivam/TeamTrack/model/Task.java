package com.shivam.TeamTrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;

    @ManyToOne
    private Project project;
    @ManyToOne
    private User assignedPerson;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    private LocalDateTime createdDate;
    private LocalDateTime dueDate;

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        COMPLETED
    }

}
