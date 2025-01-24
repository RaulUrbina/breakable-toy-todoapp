package com.springboot.todoapp_backend.service;

import com.springboot.todoapp_backend.Utilities.Constants;
import com.springboot.todoapp_backend.model.ToDo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoSearchService {
    
    public List<ToDo> getFilteredList(
            List<ToDo> toDoList,
            String text,
            ToDo.Priority priority,
            Boolean isDone,
            int page,
            String sortBy,
            String order
    ) {
        int adjustedPage = page > 0 ? page - 1 : 0;
        return toDoList.stream()
                .filter(todo -> text == null || todo.getText().toLowerCase().contains(text.toLowerCase()))
                .filter(todo -> priority == null || todo.getPriority() == priority)
                .filter(todo -> isDone == null || todo.isDone() == isDone)
                .sorted(getComparator(sortBy, order))
                .skip((long) adjustedPage * Constants.PAGE_SIZE)
                .limit(Constants.PAGE_SIZE)
                .collect(Collectors.toList());
    }

    public Integer getTotalItems(
            List<ToDo> toDoList,
            String text,
            ToDo.Priority priority,
            Boolean isDone
    ) {
        return (int) toDoList.stream()
                .filter(todo -> text == null || todo.getText().toLowerCase().contains(text.toLowerCase()))
                .filter(todo -> priority == null || todo.getPriority() == priority)
                .filter(todo -> isDone == null || todo.isDone() == isDone)
                .count();
    }

    private Comparator<ToDo> getComparator(String sortBy, String order) {
        Comparator<ToDo> comparator;

        if (sortBy != null && sortBy.equalsIgnoreCase("duedate")) {
            comparator = Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
        } else {
            comparator = Comparator.comparing(ToDo::getPriority);
        }

        if ("asc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
} 