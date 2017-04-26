package am.davsoft.propman.helpers;

/**
 * @author David Shahbazyan
 * @since Apr 21, 2017
 */
public enum TaskStatus {
    READY("Ready!"),
    STARTING("Starting..."),
    PROCESSING("Processing..."),
    DONE("Done!"),
    CANCELED("Canceled!"),
    FAILED("Failed!");

    private final String message;

    TaskStatus(String name) {
        this.message = name;
    }

    public String getMessage() {
        return message;
    }
}
