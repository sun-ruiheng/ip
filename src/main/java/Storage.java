import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class Storage {
    // load data from hard disk
    public static void loadFromFile(String filePath, TaskList taskList) {
        Parser parserStorage = new Parser("yyyy-MM-dd");
        try {
            File f = new File(filePath);
            f.createNewFile();
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String task = s.nextLine();
                boolean done = s.nextLine() == "true" ? true : false;
                String desc = s.nextLine();
                Task t;
                if (task.equals("T")) {
                    t = new Task(done, desc);
                } else if (task.equals("D")) {
                    String deadline = s.nextLine();
                    LocalDate d = parserStorage.parse(deadline);
                    t = new Task(done, desc, d);
                } else if (task.equals("E")) {
                    String start = s.nextLine();
                    String deadline = s.nextLine();
                    LocalDate st = parserStorage.parse(start);
                    LocalDate d = parserStorage.parse(deadline);
                    t = new Task(done, desc, st, d);
                } else {
                    t = null;
                    System.out.println("bad formatting in tasks.txt");
                }
                taskList.add(t);
            }
        } catch (IOException e) {
            System.out.println("An IO error occurred with the data file.");
            e.printStackTrace();
        }
    }

    public static void writeToFile(String filePath, TaskList taskList) throws IOException {
        try {
            FileWriter fw = new FileWriter(filePath);
            for (int i = 0; i < taskList.size(); i++) {
                Task t = taskList.get(i);
                fw.write(t.taskType);
                fw.write("\n");
                fw.write(t.isDone ? "true" : "false");
                fw.write("\n" + t.description + "\n");
                if (t.taskType == 'D') {
                    fw.write(t.endDate.toString() + "\n");
                } else if (t.taskType == 'E') {
                    fw.write(t.startDate.toString() + "\n");
                    fw.write(t.endDate.toString() + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Oops, IO Exception when writing to file");
        }
    }
}
