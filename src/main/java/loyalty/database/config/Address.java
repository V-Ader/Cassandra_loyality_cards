package loyalty.database.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Address {
    private Integer port;
    private String address;
}
