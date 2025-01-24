package com.springboot.todoapp_backend.service;

import com.springboot.todoapp_backend.dtos.NewToDoDTO;
import com.springboot.todoapp_backend.dtos.UpdateToDoDTO;
import com.springboot.todoapp_backend.model.ToDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ToDoService {
    private static final Logger logger = LoggerFactory.getLogger(ToDoService.class);

    private final List<ToDo> toDoList;
    private final TodoValidationService validationService;
    private final TodoSearchService searchService;
    private final TodoStatsService statsService;

    public ToDoService(
            TodoValidationService validationService,
            TodoSearchService searchService,
            TodoStatsService statsService
    ) {
        this.toDoList = new ArrayList<>();
        this.validationService = validationService;
        this.searchService = searchService;
        this.statsService = statsService;
    }

    public Optional<ToDo> getItem(String id) {
        return toDoList.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public List<ToDo> getFilteredList(
            String text,
            ToDo.Priority priority,
            Boolean isDone,
            int page,
            String sortBy,
            String order
    ) {
        return searchService.getFilteredList(toDoList, text, priority, isDone, page, sortBy, order);
    }

    public Integer getTotalItems(
            String text,
            ToDo.Priority priority,
            Boolean isDone,
            String sortBy
    ) {
        return searchService.getTotalItems(toDoList, text, priority, isDone);
    }

    public ToDo addItem(NewToDoDTO newToDo) {
        validationService.validateNewTodo(newToDo);
        
        LocalDate dueDate = Optional.ofNullable(newToDo.getDueDate())
                .filter(d -> !d.isEmpty())
                .map(LocalDate::parse)
                .orElse(null);

        ToDo newItem = ToDo.builder()
                .text(newToDo.getText())
                .priority(newToDo.getPriority())
                .dueDate(dueDate)
                .build();

        toDoList.add(newItem);
        logger.info("New item created with ID: {}", newItem.getId());
        return newItem;
    }

    public Optional<ToDo> updateItem(String id, UpdateToDoDTO request) {
        validationService.validateUpdateTodo(request);
        Optional<ToDo> existingItem = getItem(id);

        existingItem.ifPresent(todo -> {
            if (Objects.nonNull(request.getText())) {
                todo.setText(request.getText());
            }
            if (Objects.nonNull(request.getPriority())) {
                todo.setPriority(request.getPriority());
            }
            if (request.getDueDate() == null || request.getDueDate().isEmpty()) {
                todo.setDueDate(null);
            } else {
                todo.setDueDate(LocalDate.parse(request.getDueDate()));
            }
            logger.info("Item updated with ID: {}", id);
        });
        
        return existingItem;
    }

    public Optional<ToDo> markAsDone(String id) {
        Optional<ToDo> existingItem = getItem(id);

        existingItem.ifPresent(todo -> {
            if (!todo.isDone()) {
                todo.setDone(true);
                todo.setDoneDate(LocalDateTime.now());
                logger.info("Item marked as done with ID: {}", id);
            }
        });
        return existingItem;
    }

    public Optional<ToDo> markAsUndone(String id) {
        Optional<ToDo> existingItem = getItem(id);
        existingItem.ifPresent(todo -> {
            if (todo.isDone()) {
                todo.setDone(false);
                todo.setDoneDate(null);
                logger.info("Item marked as undone with ID: {}", id);
            }
        });
        return existingItem;
    }

    public Optional<ToDo> deleteItem(String id) {
        Optional<ToDo> existingItem = getItem(id);

        existingItem.ifPresent(todo -> {
            toDoList.remove(todo);
            logger.info("Item deleted with ID: {}", id);
        });

        return existingItem;
    }

    public Map<String, String> getCompletionStats() {
        return statsService.getCompletionStats(toDoList);
    }
}
