package loyalty.actors.watcher;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.db_operators.CardByClientTable;
import loyalty.db_operators.CardByIssuerTable;
import loyalty.db_operators.LogsByIssuerTable;
import loyalty.db_operators.TokensTable;
import loyalty.models.CardId;
import loyalty.models.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Watcher {
    public static List<WatcherInspectionResult> inspect(CqlSession session, CassandraConnectionConfig config) {
        Set<CardId> cards = LogsByIssuerTable.getAllLoggedCards(session, config);
        List<WatcherInspectionResult> results = new LinkedList<>();
        for (CardId cardId : cards) {
            long tokenValue = TokensTable.getTokenValue(session, config, cardId.getIssuerEmail(), cardId.getClientEmail());

            if (tokenValue < 0) { // alert & set to invalid
//                System.out.printf("Card: %s - %s has invalid value. Invalid value %d:\n", cardId.getIssuerEmail(), cardId.getClientEmail(), tokenValue);
                CardByIssuerTable.setStatus(session, config, cardId.getIssuerEmail(), cardId.getClientEmail(), "INVALID");
                CardByClientTable.setStatus(session, config, cardId.getClientEmail(), cardId.getIssuerEmail(), "INVALID");
                List<Log> invalidOperations = LogsByIssuerTable.getLogsByTotalChange(session, cardId.getIssuerEmail(), cardId.getClientEmail(), (int) Math.abs(tokenValue));
                sendAlert(cardId.getIssuerEmail(), cardId.getClientEmail(), invalidOperations);
                results.add(new WatcherInspectionResult(new CardId(cardId.getIssuerEmail(), cardId.getClientEmail()), tokenValue, invalidOperations));
            } else if (tokenValue == 0) { // set to inactive
                CardByIssuerTable.setStatus(session, config, cardId.getIssuerEmail(), cardId.getClientEmail(), "INACTIVE");
                CardByClientTable.setStatus(session, config, cardId.getClientEmail(), cardId.getIssuerEmail(), "INACTIVE");
            }
        }
        return results;
    }

    private static void sendAlert(String issuerEmail, String clientEmail, List<Log> logs) {
//        System.out.printf("Card: %s - %s has invalid value. Invalid operations:\n", issuerEmail, clientEmail);
//        for (Log log : logs) {
//            System.out.printf(log.toString());
//        }
    }
}
