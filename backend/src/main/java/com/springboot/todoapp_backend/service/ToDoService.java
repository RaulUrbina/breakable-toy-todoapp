package com.springboot.todoapp_backend.service;

import com.springboot.todoapp_backend.Utilities.Constants;
import com.springboot.todoapp_backend.dtos.NewToDoDTO;
import com.springboot.todoapp_backend.dtos.UpdateToDoDTO;
import com.springboot.todoapp_backend.model.ToDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ToDoService {
    private static final Logger logger = LoggerFactory.getLogger(ToDoService.class);

    private final List<ToDo> toDoList;

    public ToDoService(){
        this.toDoList = new ArrayList<>();
    }

    public Optional<ToDo> getItem(String id) {
        Optional<ToDo> optional = Optional.empty();
        for (ToDo item : toDoList){
            if(item.getId().equals(id)){
                optional = Optional.of(item);
                return optional;
            }
        }
        return optional;
    }

    public List<ToDo> getFilteredList(
            String text,
            ToDo.Priority priority,
            Boolean isDone,
            int page,
            String sortBy,
            String order
    ) {
        int adjustedPage = page > 0 ? page - 1 : 0;
        return toDoList.stream()
                // Text filter
                .filter(todo -> text == null || todo.getText().toLowerCase().contains(text.toLowerCase()))
                // Priority filter
                .filter(todo -> priority == null || todo.getPriority() == priority)
                // Status filter
                .filter(todo -> isDone == null || todo.isDone() == isDone)
                // Sort by priority and/or dueDate
                .sorted(getComparator(sortBy, order))
                // Pagination
                .skip((long) adjustedPage * Constants.PAGE_SIZE)
                .limit(Constants.PAGE_SIZE)
                .collect(Collectors.toList());
    }

    public Integer getTotalItems(
            String text,
            ToDo.Priority priority,
            Boolean isDone,
            String sortBy
    ) {
        List<ToDo> filteredTodos = toDoList.stream()
                .filter(todo -> text == null || todo.getText().toLowerCase().contains(text.toLowerCase()))
                .filter(todo -> priority == null || todo.getPriority() == priority)
                .filter(todo -> isDone == null || todo.isDone() == isDone)
                .sorted(getComparator(sortBy, "desc"))
                .toList();

        return filteredTodos.size();
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
        //If sortBy is null || priority || invalid
        return comparator;
    }

    public ToDo addItem(NewToDoDTO newToDo){
        LocalDate dueDate = Optional.ofNullable(newToDo.getDueDate())
                .filter(d -> !d.isEmpty())
                .map(LocalDate::parse)
                .orElse(null);

        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The due date must be today or a future date.");
        }

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
        Optional<ToDo> existingItem = getItem(id);

        if (request.getText() == null && request.getPriority() == null) {
            throw new IllegalArgumentException("No valid fields provided for update");
        }

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
                LocalDate newDueDate = LocalDate.parse(request.getDueDate());
                if (newDueDate.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("The due date must be today or a future date.");
                } else {
                    todo.setDueDate(newDueDate);
                }
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

    public Map<String, String> getCompletionStats(){
        long totalMillis = 0;
        long totalHighPriorityMillis = 0;
        long totalMediumPriorityMillis = 0;
        long totalLowPriorityMillis = 0;

        int totalTasks = 0;
        int highPriorityTasks = 0;
        int mediumPriorityTasks = 0;
        int lowPriorityTasks = 0;

        for (ToDo todo : toDoList) {
            if (todo.isDone() && todo.getDoneDate() != null && todo.getCreationDate() != null) {
                Duration duration = Duration.between(todo.getCreationDate(), todo.getDoneDate());

                long millisTaken = duration.toMillis();

                totalMillis += millisTaken;
                totalTasks++;

                switch (todo.getPriority()) {
                    case HIGH:
                        totalHighPriorityMillis += millisTaken;
                        highPriorityTasks++;
                        break;
                    case MEDIUM:
                        totalMediumPriorityMillis += millisTaken;
                        mediumPriorityTasks++;
                        break;
                    case LOW:
                        totalLowPriorityMillis += millisTaken;
                        lowPriorityTasks++;
                        break;
                }
            }
        }

        long averageMillis = totalTasks > 0 ? totalMillis / totalTasks : 0;
        long averageHighPriorityMillis = highPriorityTasks > 0 ? totalHighPriorityMillis / highPriorityTasks : 0;
        long averageMediumPriorityMillis = mediumPriorityTasks > 0 ? totalMediumPriorityMillis / mediumPriorityTasks : 0;
        long averageLowPriorityMillis = lowPriorityTasks > 0 ? totalLowPriorityMillis / lowPriorityTasks : 0;

        Map<String, String> result = new HashMap<>();
        result.put("averageTime", formatMillisToStandardTime(averageMillis));
        result.put("averageTimeHighPriority", formatMillisToStandardTime(averageHighPriorityMillis));
        result.put("averageTimeMediumPriority", formatMillisToStandardTime(averageMediumPriorityMillis));
        result.put("averageTimeLowPriority", formatMillisToStandardTime(averageLowPriorityMillis));


        return result;

    }

    private String formatMillisToStandardTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }






}
