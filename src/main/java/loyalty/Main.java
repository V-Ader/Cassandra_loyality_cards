package loyalty;

import loyalty.actors.client.ClientSession;
import loyalty.database.ConnectionException;

public class Main {
    public static void main(String[] args) {
        ClientSession session;
        String clientEmail = "owner1@example.com";
        String issuerEmail = "issuer1@example.com";
        try {
            session = new ClientSession(clientEmail, issuerEmail);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client connected!");


        session.closeConnection();
        System.out.println("Client disconnected!");

    }
}