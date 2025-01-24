package loyalty.database;


import com.datastax.oss.driver.api.core.session.Session;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CassandraConnection {
    private Session session;

    public void close() {
        session.close();
    }
}
