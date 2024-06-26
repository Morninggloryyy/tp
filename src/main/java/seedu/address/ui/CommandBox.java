package seedu.address.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;



/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;

    @FXML
    private Text suggestionsText;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles the keyTyped event.
     */
    @FXML
    private void handleTextChanged() {
        suggestionsText.setText("");
        String currentText = commandTextField.getText().toLowerCase();
        List<String> suggestions = generateSuggestions(currentText);
        updateSuggestions(suggestions);
    }

    /**
     * Generates suggestions from text inputs
     */
    private List<String> generateSuggestions(String commandText) {
        List<String> suggestions = new ArrayList<>();
        String commandInput = commandText.trim();
        final List<String> commandList = Arrays.asList(
                "add", "list", "edit", "find", "delete", "clear", "addSched",
                "deleteSched", "editSched", "exit", "help"
        );

        // Check if the entered command matches any suggestions
        for (String command : commandList) {
            if (command.toLowerCase().startsWith(commandInput)
                    || command.toLowerCase().contains(commandInput)) {
                suggestions.add(command);
            }
        }
        suggestions.removeIf(suggest -> suggest.equals(commandInput));
        return suggestions;
    }

    /**
     * Populate the suggestionText with generated suggestions
     */

    public void updateSuggestions(List<String> suggestions) {
        StringBuilder suggestionsBuilder = new StringBuilder();
        for (String suggestion : suggestions) {
            suggestionsBuilder.append(suggestion).append(" ");
        }
        suggestionsText.setText(suggestionsBuilder.toString());
    }
    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
