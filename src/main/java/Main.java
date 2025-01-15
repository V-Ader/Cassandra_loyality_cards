import actors.client.ClientSession;
import database.ConnectionException;

public class Main {
    public static void main(String[] args) {
        ClientSession session;
        try {
            session = new ClientSession();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client connected!");


        session.closeConnection();
        System.out.println("Client disconnected!");

    }
}
