package eu.gaiax.federatedcatalogue.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RuleRequest {
    private String rule;
    private String dest;
    private String source;
}
