package loyalty.actors.watcher;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.db_operators.CardByClientTable;
import loyalty.db_operators.CardByIssuerTable;
import loyalty.db_operators.LogsByIssuerTable;
import loyalty.db_operators.TokensTable;
import loyalty.models.CardId;
import loyalty.models.Log;

import java.util.List;
import java.util.Set;

public class Watcher {
    public void inspect(CqlSession session, CassandraConnectionConfig config) {
        Set<CardId> cards = LogsByIssuerTable.getAllLoggedCards(session, config);
        for (CardId cardId : cards) {
            long tokenValue = TokensTable.getTokenValue(session, config, cardId.getIssuerEmail(), cardId.getClientEmail());

            if (tokenValue < 0) { // alert & set to invalid
                CardByIssuerTable.setStatus(session, config, cardId.getIssuerEmail(), cardId.getClientEmail(), "INVALID");
                CardByClientTable.setStatus(session, config, cardId.getClientEmail(), cardId.getIssuerEmail(), "INVALID");
                List<Log> invalidOperations = LogsByIssuerTable.getLogsByTotalChange(session, cardId.getIssuerEmail(), cardId.getClientEmail(), Math.abs(tokenValue));
                this.sendAlert(cardId.getIssuerEmail(), cardId.getClientEmail(), invalidOperations);
            } else if (tokenValue == 0) { // set to inactive
                CardByIssuerTable.setStatus(session, config, cardId.getIssuerEmail(), cardId.getClientEmail(), "INACTIVE");
                CardByClientTable.setStatus(session, config, cardId.getClientEmail(), cardId.getIssuerEmail(), "INACTIVE");
            }
        }
    }

    private void sendAlert(String issuerEmail, String clientEmail, List<Log> logs) {
        System.out.printf("Card: %s - %s has invalid value. Invalid operations:\n", issuerEmail, clientEmail);
        for (Log log : logs) {
            System.out.printf(log.toString());
        }
    }


}
