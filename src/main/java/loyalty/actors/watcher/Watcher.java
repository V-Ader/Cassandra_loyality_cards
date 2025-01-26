package loyalty.actors.watcher;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.database.config.CassandraConnectionConfig;
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
                // Handle the case where tokenValue is negative
            } else if (tokenValue == 0) { // set to inactive
            }


        }
    }


}
