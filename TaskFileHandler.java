import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple file handler using a pipe-separated text format.
 * Each line: id|title|description|dueDate(YYYY-MM-DD)|completed
 */
public class TaskFileHandler {
    private final File file;

    public TaskFileHandler(String filename) {
        this.file = new File(filename);
    }

    public void saveTasks(List<Task> tasks) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Task t : tasks) {
                bw.write(t.serialize());
                bw.newLine();
            }
        }
    }

    public List<Task> loadTasks() throws IOException {
        List<Task> out = new ArrayList<>();
        if (!file.exists()) return out;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Task t = Task.deserialize(line);
                if (t != null) out.add(t);
            }
        }
        return out;
    }
}
