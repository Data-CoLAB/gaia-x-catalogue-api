package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.N_DATA_ACCOUNT_EXPORT;

@Node(labels = N_DATA_ACCOUNT_EXPORT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataAccountExport extends BaseEntity {
    private String accessType;
    private String requestType;
    private Set<String> formatType = new HashSet<>();
}
