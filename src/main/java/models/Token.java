package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Token {
    private String ownerEmail;
    private String issuerEmail;
    private long tokens;
}
