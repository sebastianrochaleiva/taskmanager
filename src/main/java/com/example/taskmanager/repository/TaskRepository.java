package com.example.taskmanager.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.example.taskmanager.model.Task;

@Repository
public interface TaskRepository extends ReactiveMongoRepository <Task, String>{
    //Puedes agregar métodos custom si lo necesitas más adelante
}
