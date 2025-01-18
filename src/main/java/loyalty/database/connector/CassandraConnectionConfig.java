package loyalty.database.connector;

import com.datastax.driver.core.ConsistencyLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class CassandraConnectionConfig {
    ConsistencyLevel writeConsistency;
    ConsistencyLevel readConsistency;

    public static CassandraConnectionConfig getDefaultConfig(){
        return CassandraConnectionConfig.builder()
                        .readConsistency(ConsistencyLevel.QUORUM)
                        .writeConsistency(ConsistencyLevel.QUORUM)
                        .build();
    }
}
