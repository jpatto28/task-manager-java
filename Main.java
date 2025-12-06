import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface / driver for the Task Manager.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        manager.load();
        System.out.println("Welcome to Task Manager!");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("Enter choice (1-6):").trim();
            switch (choice) {
                case "1":
                    handleAdd(manager);
                    break;
                case "2":
                    handleView(manager);
                    break;
                case "3":
                    handleUpdate(manager);
                    break;
                case "4":
                    handleDelete(manager);
                    break;
                case "5":
                    manager.save();
                    System.out.println("Tasks saved.");
                    break;
                case "6":
                    manager.save();
                    System.out.println("Tasks saved. Exiting. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1) Add Task");
        System.out.println("2) View Tasks");
        System.out.println("3) Update Task");
        System.out.println("4) Delete Task");
        System.out.println("5) Save Tasks");
        System.out.println("6) Save & Exit");
    }

    private static void handleAdd(TaskManager manager) {
        System.out.println("\n--- Add Task ---");
        String title = prompt("Title:");
        String desc = prompt("Description:");
        LocalDate due = promptDate("Due date (YYYY-MM-DD) or leave blank:");
        Task t = manager.addTask(title, desc, due);
        System.out.println("Added: " + t);
    }

    private static void handleView(TaskManager manager) {
        System.out.println("\n--- View Tasks ---");
        System.out.println("1) All tasks");
        System.out.println("2) Pending only");
        System.out.println("3) Completed only");
        String c = prompt("Choose view option (1-3):").trim();
        List<Task> list;
        switch (c) {
            case "2":
                list = manager.getTasksFiltered(false);
                break;
            case "3":
                list = manager.getTasksFiltered(true);
                break;
            default:
                list = manager.getTasks();
        }
        if (list.isEmpty()) {
            System.out.println("No tasks to display.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private static void handleUpdate(TaskManager manager) {
        System.out.println("\n--- Update Task ---");
        int id = promptInt("Enter Task ID to update:");
        Task found = manager.findTaskById(id);
        if (found == null) {
            System.out.println("Task with ID " + id + " not found.");
            return;
        }
        System.out.println("Found: " + found);
        String newTitle = prompt("New title (leave blank to keep):");
        String newDesc = prompt("New description (leave blank to keep):");
        LocalDate newDue = promptDate("New due date (YYYY-MM-DD) or leave blank to keep:");
        String comp = prompt("Mark completed? (y/n or blank to keep):").trim().toLowerCase();

        Boolean newCompleted = null;
        if (comp.equals("y") || comp.equals("yes")) newCompleted = true;
        else if (comp.equals("n") || comp.equals("no")) newCompleted = false;

        boolean success = manager.updateTask(id,
                newTitle.isEmpty() ? null : newTitle,
                newDesc.isEmpty() ? null : newDesc,
                newDue,
                newCompleted);
        if (success) System.out.println("Task updated: " + manager.findTaskById(id));
        else System.out.println("Failed to update task.");
    }

    private static void handleDelete(TaskManager manager) {
        System.out.println("\n--- Delete Task ---");
        int id = promptInt("Enter Task ID to delete:");
        Task found = manager.findTaskById(id);
        if (found == null) {
            System.out.println("Task with ID " + id + " not found.");
            return;
        }
        System.out.println("Found: " + found);
        String confirm = prompt("Are you sure you want to delete this task? (y/n):").trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            boolean removed = manager.deleteTask(id);
            if (removed) System.out.println("Task deleted.");
            else System.out.println("Failed to delete task.");
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private static String prompt(String message) {
        System.out.print(message + " ");
        return scanner.nextLine();
    }

    private static int promptInt(String message) {
        while (true) {
            String s = prompt(message).trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static LocalDate promptDate(String message) {
        String s = prompt(message).trim();
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD or leave blank.");
            return promptDate(message); // re-prompt
        }
    }
}
