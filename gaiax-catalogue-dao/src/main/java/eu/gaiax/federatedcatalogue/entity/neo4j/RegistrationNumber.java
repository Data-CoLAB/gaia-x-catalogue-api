package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.N_REGISTRATION_NUMBER;

@Node(labels = N_REGISTRATION_NUMBER)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationNumber extends BaseEntity {
    private String type;
    private String number;
    private String countryCode;
}
