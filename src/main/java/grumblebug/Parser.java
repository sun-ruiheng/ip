package grumblebug;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;

/**
 * Parser object is used to make sense of the user inputs.
 * For example, it can help to parse reasonable date formats flexibly
 * into LocalDate objects, from user and for storage into data file.
 */
public class Parser {

    private DateTimeFormatter formatter;

    Parser(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    /**
     * Parses date in the specified format into a LocalDate object, to be stored
     * into a Task object.
     * 
     * @param dt the String date input.
     * @return the LocalDate with the parsed time stored.
     */
    public LocalDate parseDate(String dt) {
        return LocalDate.parse(dt, this.formatter);
    }

    public String parse(String input, TaskList taskList, Storage storage) {
        if (input.equals("list")) { // show the list!
            return taskList.getTasks();
        } else if (input.equals("bye")) {
            Platform.exit();
            return "";
        } else if (input.equals("save")) {
            return storage.writeToFile(taskList);
        } else if (input.startsWith("mark")) {
            return processMarkTaskInput(input, true, GrumbleBug.NUM_PARAMS_FOR_MARK, taskList);
        } else if (input.startsWith("unmark")) {
            return processMarkTaskInput(input, false, GrumbleBug.NUM_PARAMS_FOR_MARK, taskList);
        } else if (input.startsWith("find")) {
            return processFindTasksInput(input, GrumbleBug.NUM_PARAMS_FOR_FIND, taskList);
        } else if (input.startsWith("todo")) { // add to list
            return processTodoInput(input, GrumbleBug.NUM_PARAMS_FOR_TODO, taskList);
        } else if (input.startsWith("deadline")) { // add to list
            return processDeadlineInput(input, GrumbleBug.NUM_PARAMS_FOR_DEADLINE, taskList);
        } else if (input.startsWith("event")) { // add to list
            return processEventInput(input, GrumbleBug.NUM_PARAMS_FOR_EVENT, taskList);
        } else if (input.startsWith("delete")) {
            return processDeleteInput(input, GrumbleBug.NUM_PARAMS_FOR_DELETE, taskList);
        } else {
            return "I don't understand what you just said, stupid...";
        }
    }

    /**
     * Parses user input and tells taskList to mark or unmark the corresponding task,
     * if the input format is legible.
     * 
     * @param input    The user input into the chat.
     * @param doneness Whether to mark or unmark the task.
     * @return Confirmation message if successful or a scolding if input format was
     *         poor.
     */
    private String processMarkTaskInput(String input, boolean doneness, int numParams, TaskList taskList) {
        String[] words = input.split(" ", numParams);
        assert words.length <= numParams;
        if (words.length < numParams || words[1].length() < 1) {
            return "Give a number you silly goose, stupid!";
        }
        try {
            int i = Integer.parseInt(words[1]);
            if (i > taskList.size()) {
                return "Out of bounds. Get your head in the game, please.";
            }
            if (doneness) {
                taskList.mark(i);
            } else {
                taskList.unmark(i);
            }
            return "Ok it's done. What else do you want...";
        } catch (NumberFormatException e) {
            return "That was not understood. Silly.";
        }
    }

    /**
     * Parses user input and tells taskList to find the tasks using the keyword.
     * 
     * @param input The user input into the chat.
     * @return The list of matching tasks as found by taskList.
     */
    public String processFindTasksInput(String input, int numParams, TaskList taskList) {
        String[] words = input.split(" ", numParams);
        assert words.length <= numParams;
        if (words.length < numParams || words[1].length() < 1) {
            return "Give a non-empty search query, you stupid!";
        }
        return taskList.findMatches(words[1]);
    }

    /**
     * Parses user input and tells taskList to add the To-do as requested.
     * 
     * @param input The user input into the chat.
     * @return A confirmation message of the successful action.
     */
    private String processTodoInput(String input, int numParams, TaskList taskList) {
        String[] words = input.split(" ", numParams);
        assert words.length <= numParams;
        if (words.length < numParams || words[1].length() < 1) {
            return "Give a non-empty name for the task, you stupid!";
        }
        taskList.addToDo(words[1]);
        return "k added...";

    }

    /**
     * Parses user input and, if input is in acceptable format,
     * activates taskList to add the corresponding Deadline as requested.
     * 
     * @param input The user input into the chat.
     * @return A confirmation message of the addition, or a scolding if input is
     *         improper.
     */
    private String processDeadlineInput(String input, int numParams, TaskList taskList) {
        String[] words = input.split(" ", numParams);
        assert words.length <= numParams;
        if (words.length < numParams) {
            return "Give the correct number of arguments! This should be simple by now.";
        }
        try {
            taskList.addDeadline(words[1], this.parseDate(words[2]));
            return "k";
        } catch (DateTimeParseException e) {
            return "Ugh, I don't get it. Date should be in yyyy-MM-dd format... And keep the title spaceless!";
        }
    }

    /**
     * Parses user input and, if input is in acceptable format,
     * activates taskList to add the corresponding Event as requested.
     * 
     * @param input The user input into the chat.
     * @return A confirmation message of the addition, or a scolding if input is
     *         improper.
     */
    private String processEventInput(String input, int numParams, TaskList taskList) {
        String[] words = input.split(" ", numParams);
        assert words.length <= numParams;
        if (words.length < numParams) {
            return "Give the correct number of arguments! This should be simple by now.";
        }
        try {
            LocalDate startDate = this.parseDate(words[2]);
            LocalDate endDate = this.parseDate(words[3]);
            if (endDate.isBefore(startDate)) {
                return "I don't think we can time-travel. How come it ends before it starts? Lol...";
            }
            taskList.addEvent(words[1], startDate, endDate);
            return "k";
        } catch (DateTimeParseException e) {
            return "Ugh, I don't get it. Date should be in yyyy-MM-dd format... And keep the title spaceless!";
        }
    }

    /**
     * Parses user input and, if input is in acceptable format,
     * activates taskList to delete the corresponding task as requested.
     * 
     * @param input The user input into the chat.
     * @return A confirmation message of deletion, or a scolding if input is
     *         improper.
     */
    private String processDeleteInput(String input, int numParams, TaskList taskList) {
        String[] words = input.split(" ", numParams);
        assert words.length <= numParams;
        if (words.length < numParams) {
            return "Give the correct number of arguments! This should be simple by now.";
        }
        try {
            int i = Integer.parseInt(words[1]);
            taskList.delete(i);
            return "Ok it's gone. What else do you want...";
        } catch (NumberFormatException e) {
            return "That was not understood. Silly.";
        }
    }

}
