package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RuleRequest {
    private String rel;
    private String dst;
    private String src;
}
