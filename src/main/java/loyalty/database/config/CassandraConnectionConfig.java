package loyalty.database.config;

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

    public static CassandraConnectionConfig getDefault(){
        return CassandraConnectionConfig.builder()
                .writeConsistency(ConsistencyLevel.QUORUM)
                .readConsistency(ConsistencyLevel.QUORUM)
                .build();
    }

    public static CassandraConnectionConfig getConsistencyOne(){
        return CassandraConnectionConfig.builder()
                .writeConsistency(ConsistencyLevel.ONE)
                .readConsistency(ConsistencyLevel.ONE)
                .build();
    }
}
