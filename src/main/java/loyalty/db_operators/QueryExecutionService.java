package loyalty.db_operators;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverTimeoutException;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.servererrors.ReadTimeoutException;
import com.datastax.oss.driver.api.core.servererrors.UnavailableException;
import com.datastax.oss.driver.api.core.servererrors.WriteTimeoutException;

import java.util.Objects;

public class QueryExecutionService {
    public static ResultSet execute(CqlSession session, BoundStatement boundStatement)  {
        for(int retry = 0; retry < 5; retry++){
            try {
                return session.execute(boundStatement);
            } catch (DriverTimeoutException | ReadTimeoutException | WriteTimeoutException e) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (UnavailableException e) {
                System.err.println("UnavailableException: brak wystarczającej liczby replik dla danego consistency level. Sprawdź konfigurację klastra i poziom spójności.");
            } catch (Exception e) {
                System.err.println("Nieoczekiwany błąd: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ResultSet executeOnFlexibleConsistency(CqlSession session, BoundStatement boundStatement) {
        for(int retry = 0; retry < 5; retry++){
            try {
                return session.execute(boundStatement);
            } catch (DriverTimeoutException | ReadTimeoutException | WriteTimeoutException e) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (UnavailableException e) {
                if (Objects.equals(ConsistencyLevel.ALL, boundStatement.getConsistencyLevel())) {
                    boundStatement = boundStatement.setConsistencyLevel(ConsistencyLevel.QUORUM);
                } else {
                    boundStatement = boundStatement.setConsistencyLevel(ConsistencyLevel.ONE);
                }
            } catch (Exception e) {
                System.err.println("Nieoczekiwany błąd: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
}
