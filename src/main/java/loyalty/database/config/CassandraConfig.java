package loyalty.database.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class CassandraConfig {
    private List<Address> addresses;
    private final String keyspace;
}

