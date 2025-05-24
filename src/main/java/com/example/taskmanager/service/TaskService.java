package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {
    Flux<Task> getAllTasks();
    Mono<Task> getTaskById(String id);
    Mono<Task> createTask(Task task);
    Mono<Task> updateTask(String id, Task task);
    Mono<Void> deleteTask(String id);
}
