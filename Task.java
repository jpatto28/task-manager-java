import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a simple Task with id, title, description, due date, and completion status.
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate; // nullable
    private boolean completed;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Task(int id, String title, String description, LocalDate dueDate, boolean completed) {
        this.id = id;
        this.title = title == null ? "" : title.trim();
        this.description = description == null ? "" : description.trim();
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public Task(int id, String title, String description, LocalDate dueDate) {
        this(id, title, description, dueDate, false);
    }

    public int getId() {
        return id;
    }

    // id has no setter to preserve identity once created

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markComplete() {
        this.completed = true;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Return a single-line string representation suitable for display.
     */
    @Override
    public String toString() {
        String due = (dueDate == null) ? "No due date" : dueDate.format(FORMATTER);
        String status = completed ? "Completed" : "Pending";
        return String.format("ID: %d | %s | %s | Due: %s | %s", id, title, description, due, status);
    }

    /**
     * Serialize into a safe pipe-separated format (escaping pipes).
     */
    public String serialize() {
        return String.format("%d|%s|%s|%s|%b",
                id,
                escape(title),
                escape(description),
                (dueDate == null ? "" : dueDate.format(FORMATTER)),
                completed);
    }

    /**
     * Create Task from serialized form (used by TaskFileHandler).
     */
    public static Task deserialize(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|", -1); // keep empty strings
        if (parts.length != 5) return null;
        try {
            int id = Integer.parseInt(parts[0]);
            String title = unescape(parts[1]);
            String description = unescape(parts[2]);
            LocalDate due = parts[3].isEmpty() ? null : LocalDate.parse(parts[3], FORMATTER);
            boolean completed = Boolean.parseBoolean(parts[4]);
            return new Task(id, title, description, due, completed);
        } catch (Exception e) {
            return null;
        }
    }

    private static String escape(String input) {
        if (input == null) return "";
        return input.replace("|", "\\|").replace("\n", "\\n").replace("\r", "");
    }

    private static String unescape(String input) {
        if (input == null) return "";
        return input.replace("\\|", "|").replace("\\n", "\n");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
