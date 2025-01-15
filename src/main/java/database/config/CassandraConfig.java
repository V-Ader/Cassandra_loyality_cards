package database.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class CassandraConfig {
    private final Integer port;
    private List<String> addresses;
}
