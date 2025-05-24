package com.example.taskmanager.service;

import org.springframework.stereotype.Service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;

    @Override
    public Flux<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    @Override
    public Mono<Task> getTaskById(String id){
        return taskRepository.findById(id);
    }

    @Override
    public Mono<Task> createTask(Task task){
        return taskRepository.save(task);
    }

    @Override
    public Mono<Task> updateTask(String id, Task task){
        return taskRepository.findById(id)
            .flatMap(existingTask -> {
                existingTask.setTitle(task.getTitle());
                existingTask.setDescription(task.getDescription());
                existingTask.setCompleted(task.isCompleted());
                return taskRepository.save(existingTask);
            });
    }

    @Override
    public Mono<Void> deleteTask(String id){
        return taskRepository.deleteById(id);
    }
}
