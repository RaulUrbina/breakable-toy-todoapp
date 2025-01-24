package com.springboot.todoapp_backend.service;

import com.springboot.todoapp_backend.model.ToDo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TodoStatsService {
    
    public Map<String, String> getCompletionStats(List<ToDo> toDoList) {
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