package com.example.taskmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    private String title;
    private String description;
    private boolean completed;
}
