package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.N_TERMS_AND_CONDITIONS;

@Node(labels = N_TERMS_AND_CONDITIONS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsAndCondition extends BaseEntity {
    private String hash;
    private String url;
}
