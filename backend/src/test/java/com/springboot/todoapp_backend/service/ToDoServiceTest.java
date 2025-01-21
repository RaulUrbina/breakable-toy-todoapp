package com.springboot.todoapp_backend.service;

import com.springboot.todoapp_backend.dtos.NewToDoDTO;
import com.springboot.todoapp_backend.dtos.UpdateToDoDTO;
import com.springboot.todoapp_backend.model.ToDo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ToDoServiceTest {

    private ToDoService toDoService;

    @BeforeEach
    void setUp() {
        toDoService = new ToDoService();
    }

    @Nested
    class AddItemTests {
        @Test
        void testAddItem_Success() {
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.HIGH, null);

            ToDo createdToDo = toDoService.addItem(newToDo);

            assertNotNull(createdToDo);
            assertEquals("Test task", createdToDo.getText());
            assertEquals(ToDo.Priority.HIGH, createdToDo.getPriority());
            assertFalse(createdToDo.isDone());
            assertNotNull(createdToDo.getCreationDate());
        }

        @Test
        void testAddItem_WithDueDate_Success() {
            String tomorrow = LocalDate.now().plusDays(1).toString();
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.HIGH, tomorrow);

            ToDo createdToDo = toDoService.addItem(newToDo);

            assertEquals(LocalDate.parse(tomorrow), createdToDo.getDueDate());
        }

        @Test
        void testAddItem_WithPastDueDate_ThrowsException() {
            String yesterday = LocalDate.now().minusDays(1).toString();
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.HIGH, yesterday);

            assertThrows(IllegalArgumentException.class, () -> toDoService.addItem(newToDo));
        }
    }

    @Nested
    class GetAndUpdateTests {
        @Test
        void testGetItem_NotFound() {
            Optional<ToDo> item = toDoService.getItem("non-existing-id");
            assertTrue(item.isEmpty());
        }

        @Test
        void testUpdateItem_Success() {
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.MEDIUM, null);
            ToDo createdToDo = toDoService.addItem(newToDo);

            toDoService.updateItem(createdToDo.getId(), new UpdateToDoDTO("Updated task", ToDo.Priority.HIGH, null));

            Optional<ToDo> updatedToDo = toDoService.getItem(createdToDo.getId());
            assertTrue(updatedToDo.isPresent());
            assertEquals("Updated task", updatedToDo.get().getText());
            assertEquals(ToDo.Priority.HIGH, updatedToDo.get().getPriority());
        }

        @Test
        void testUpdateItem_WithInvalidDueDate_ThrowsException() {
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.MEDIUM, null);
            ToDo createdToDo = toDoService.addItem(newToDo);

            String yesterday = LocalDate.now().minusDays(1).toString();
            UpdateToDoDTO updateRequest = new UpdateToDoDTO("Updated task", ToDo.Priority.HIGH, yesterday);

            assertThrows(IllegalArgumentException.class, 
                () -> toDoService.updateItem(createdToDo.getId(), updateRequest));
        }

        @Test
        void testUpdateItem_WithNoValidFields_ThrowsException() {
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.MEDIUM, null);
            ToDo createdToDo = toDoService.addItem(newToDo);

            UpdateToDoDTO updateRequest = new UpdateToDoDTO(null, null, null);

            assertThrows(IllegalArgumentException.class, 
                () -> toDoService.updateItem(createdToDo.getId(), updateRequest));
        }
    }

    @Nested
    class StatusUpdateTests {
        @Test
        void testMarkAsDone_Success() {
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.MEDIUM, null);
            ToDo createdToDo = toDoService.addItem(newToDo);

            Optional<ToDo> markedAsDone = toDoService.markAsDone(createdToDo.getId());

            assertTrue(markedAsDone.isPresent());
            assertTrue(markedAsDone.get().isDone());
            assertNotNull(markedAsDone.get().getDoneDate());
        }

        @Test
        void testMarkAsUndone_Success() {
            NewToDoDTO newToDo = new NewToDoDTO("Test task", ToDo.Priority.MEDIUM, null);
            ToDo createdToDo = toDoService.addItem(newToDo);
            toDoService.markAsDone(createdToDo.getId());

            Optional<ToDo> markedAsUndone = toDoService.markAsUndone(createdToDo.getId());

            assertTrue(markedAsUndone.isPresent());
            assertFalse(markedAsUndone.get().isDone());
            assertNull(markedAsUndone.get().getDoneDate());
        }
    }

    @Nested
    class FilteringAndSortingTests {
        @Test
        void testGetFilteredList_WithTextFilter() {
            toDoService.addItem(new NewToDoDTO("First task", ToDo.Priority.HIGH, null));
            toDoService.addItem(new NewToDoDTO("Second task", ToDo.Priority.MEDIUM, null));
            toDoService.addItem(new NewToDoDTO("Another task", ToDo.Priority.LOW, null));

            List<ToDo> filteredList = toDoService.getFilteredList("First", null, null, 1, null, null);

            assertEquals(1, filteredList.size());
            assertEquals("First task", filteredList.get(0).getText());
        }

        @Test
        void testGetFilteredList_WithPriorityFilter() {
            toDoService.addItem(new NewToDoDTO("Task 1", ToDo.Priority.HIGH, null));
            toDoService.addItem(new NewToDoDTO("Task 2", ToDo.Priority.MEDIUM, null));
            toDoService.addItem(new NewToDoDTO("Task 3", ToDo.Priority.HIGH, null));

            List<ToDo> filteredList = toDoService.getFilteredList(null, ToDo.Priority.HIGH, null, 1, null, null);

            assertEquals(2, filteredList.size());
            assertTrue(filteredList.stream().allMatch(todo -> todo.getPriority() == ToDo.Priority.HIGH));
        }

        @Test
        void testGetFilteredList_WithStatusFilter() {
            ToDo task1 = toDoService.addItem(new NewToDoDTO("Task 1", ToDo.Priority.HIGH, null));
            ToDo task2 = toDoService.addItem(new NewToDoDTO("Task 2", ToDo.Priority.MEDIUM, null));
            toDoService.markAsDone(task1.getId());

            List<ToDo> filteredList = toDoService.getFilteredList(null, null, true, 1, null, null);

            assertEquals(1, filteredList.size());
            assertTrue(filteredList.get(0).isDone());
        }

    }

    @Nested
    class CompletionStatsTests {
        @Test
        void testGetCompletionStats_WithCompletedTasks() {
            // Add and complete tasks with different priorities
            ToDo highPriorityTask = toDoService.addItem(new NewToDoDTO("High Priority", ToDo.Priority.HIGH, null));
            ToDo mediumPriorityTask = toDoService.addItem(new NewToDoDTO("Medium Priority", ToDo.Priority.MEDIUM, null));
            ToDo lowPriorityTask = toDoService.addItem(new NewToDoDTO("Low Priority", ToDo.Priority.LOW, null));

            // Mark tasks as done
            toDoService.markAsDone(highPriorityTask.getId());
            toDoService.markAsDone(mediumPriorityTask.getId());
            toDoService.markAsDone(lowPriorityTask.getId());

            Map<String, String> stats = toDoService.getCompletionStats();

            assertNotNull(stats.get("averageTime"));
            assertNotNull(stats.get("averageTimeHighPriority"));
            assertNotNull(stats.get("averageTimeMediumPriority"));
            assertNotNull(stats.get("averageTimeLowPriority"));
        }

        @Test
        void testGetCompletionStats_WithNoCompletedTasks() {
            // Add tasks but don't complete them
            toDoService.addItem(new NewToDoDTO("Task 1", ToDo.Priority.HIGH, null));
            toDoService.addItem(new NewToDoDTO("Task 2", ToDo.Priority.MEDIUM, null));

            Map<String, String> stats = toDoService.getCompletionStats();

            assertEquals("00:00:00:00", stats.get("averageTime"));
            assertEquals("00:00:00:00", stats.get("averageTimeHighPriority"));
            assertEquals("00:00:00:00", stats.get("averageTimeMediumPriority"));
            assertEquals("00:00:00:00", stats.get("averageTimeLowPriority"));
        }
    }

    @Test
    void testDeleteItem_Success() {
        ToDo newItem = toDoService.addItem(new NewToDoDTO("Test task", ToDo.Priority.HIGH, null));
        
        Optional<ToDo> deletedItem = toDoService.deleteItem(newItem.getId());
        
        assertTrue(deletedItem.isPresent());
        assertEquals(newItem.getId(), deletedItem.get().getId());
        assertTrue(toDoService.getItem(newItem.getId()).isEmpty());
    }
}
