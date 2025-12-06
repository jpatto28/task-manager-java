import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages a collection of Task objects and provides operations on them.
 */
public class TaskManager {
    private List<Task> tasks;
    private int nextId;
    private TaskFileHandler fileHandler; // optional persistence

    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.nextId = 1;
        this.fileHandler = new TaskFileHandler("tasks.txt");
    }

    /**
     * Loads tasks from the backing file if present.
     */
    public void load() {
        try {
            List<Task> loaded = fileHandler.loadTasks();
            if (loaded != null) {
                this.tasks = new ArrayList<>(loaded);
                // set nextId to max existing id + 1
                Optional<Integer> maxId = tasks.stream().map(Task::getId).max(Comparator.naturalOrder());
                this.nextId = maxId.map(i -> i + 1).orElse(1);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load tasks (" + e.getMessage() + "). Starting with empty list.");
        }
    }

    /**
     * Saves current tasks to the backing file.
     */
    public void save() {
        try {
            fileHandler.saveTasks(tasks);
        } catch (IOException e) {
            System.out.println("Warning: could not save tasks (" + e.getMessage() + ").");
        }
    }

    public Task addTask(String title, String description, LocalDate dueDate) {
        Task t = new Task(nextId++, title, description, dueDate, false);
        tasks.add(t);
        return t;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksFiltered(Boolean completedFilter) {
        if (completedFilter == null) return getTasks();
        return tasks.stream().filter(t -> t.isCompleted() == completedFilter).collect(Collectors.toList());
    }

    public Task findTaskById(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    public boolean updateTask(int id, String newTitle, String newDescription, LocalDate newDueDate, Boolean newCompleted) {
        Task t = findTaskById(id);
        if (t == null) return false;
        if (newTitle != null) t.setTitle(newTitle);
        if (newDescription != null) t.setDescription(newDescription);
        if (newDueDate != null) t.setDueDate(newDueDate);
        if (newCompleted != null) t.setCompleted(newCompleted);
        return true;
    }

    public boolean deleteTask(int id) {
        return tasks.removeIf(t -> t.getId() == id);
    }

    public int count() {
        return tasks.size();
    }
}
