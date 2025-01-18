package loyalty;

import loyalty.actors.client.ClientSession;
import loyalty.actors.issuer.IssuerSession;
import loyalty.database.ConnectionException;

public class Main {
    public static void main(String[] args) {
        ClientSession clientSession;
        String clientEmail = "client1@example.com";
        String issuerEmail = "issuer1@example.com";
        IssuerSession issuerSession;
        try {
            clientSession = new ClientSession(clientEmail);
            issuerSession = new IssuerSession(issuerEmail);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client connected!");

        issuerSession.createCard("test2@gmail.com", 20);
        issuerSession.getAllCards();

        clientSession.closeConnection();
        issuerSession.closeConnection();

        System.out.println("Client disconnected!");

    }
}
