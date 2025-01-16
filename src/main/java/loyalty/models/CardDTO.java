package loyalty.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private String issuerEmail;
    private String ownerEmail;
    private String status;
    private long tokens;
}
