package loyalty;

import loyalty.actors.client.ClientSession;
import loyalty.actors.issuer.IssuerSession;
import loyalty.database.ConnectionException;
import loyalty.models.CardDTO;

public class Main {
    public static void main(String[] args) {
        ClientSession clientSession;
        String clientEmail = "owner1@example.com";
        String issuerEmail = "issuer1@example.com";
        IssuerSession issuerSession;
        try {
            clientSession = new ClientSession(clientEmail, issuerEmail);
            issuerSession = new IssuerSession("karol@zawislak.pl");
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client connected!");

        issuerSession.createCard("test2@gmail.com", 20);
        for(CardDTO card : issuerSession.getAllCards()) {
            System.out.println(card.toString());
        }

        clientSession.closeConnection();
        issuerSession.closeConnection();

        System.out.println("Client disconnected!");

    }
}
