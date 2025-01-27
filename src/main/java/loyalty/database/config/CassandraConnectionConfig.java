package loyalty.database.config;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class CassandraConnectionConfig {
    DefaultConsistencyLevel writeConsistency;
    DefaultConsistencyLevel readConsistency;

    public static CassandraConnectionConfig getDefault(){
        return CassandraConnectionConfig.builder()
                .writeConsistency(DefaultConsistencyLevel.ONE)
                .readConsistency(DefaultConsistencyLevel.ONE)
                .build();
    }

    public static CassandraConnectionConfig getQUORUM(){
        return CassandraConnectionConfig.builder()
                .writeConsistency(DefaultConsistencyLevel.QUORUM)
                .readConsistency(DefaultConsistencyLevel.QUORUM)
                .build();
    }

    public static CassandraConnectionConfig getConsistencyOne(){
        return CassandraConnectionConfig.builder()
                .writeConsistency(DefaultConsistencyLevel.ONE)
                .readConsistency(DefaultConsistencyLevel.ONE)
                .build();
    }

    public static CassandraConnectionConfig getConsistencyAll() {
        return CassandraConnectionConfig.builder()
                .writeConsistency(DefaultConsistencyLevel.ALL)
                .readConsistency(DefaultConsistencyLevel.ALL)
                .build();
    }
}
