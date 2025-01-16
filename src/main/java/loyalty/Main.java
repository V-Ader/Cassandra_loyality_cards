package loyalty;

import loyalty.actors.client.ClientSession;
import loyalty.actors.issuer.IssuerSession;
import loyalty.database.ConnectionException;

public class Main {
    public static void main(String[] args) {
        ClientSession session;
        String clientEmail = "owner1@example.com";
        String issuerEmail = "issuer1@example.com";
        IssuerSession session;
        try {
            session = new ClientSession(clientEmail, issuerEmail);
            session = new IssuerSession("karol@zawislak.pl");
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client connected!");

        session.createCard("test2@gmail.com", 20);
        session.getAllCards();

        session.closeConnection();
        System.out.println("Client disconnected!");

    }
}
