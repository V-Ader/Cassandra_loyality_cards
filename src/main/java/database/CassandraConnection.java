package database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CassandraConnection {
    private Cluster cluster;
    private Session session;

    public void close() {
        session.close();
        cluster.close();
    }
}
