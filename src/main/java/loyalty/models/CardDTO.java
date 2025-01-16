package loyalty.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CardDTO {
    private String issuerEmail;
    private String ownerEmail;
    private String status;
    private long tokens;
}
