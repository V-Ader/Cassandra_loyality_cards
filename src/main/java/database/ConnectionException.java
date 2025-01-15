package database;

public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Exception e) {
        super(e);
    }

    public ConnectionException(String message, Exception e) {
        super(message, e);
    }
}
