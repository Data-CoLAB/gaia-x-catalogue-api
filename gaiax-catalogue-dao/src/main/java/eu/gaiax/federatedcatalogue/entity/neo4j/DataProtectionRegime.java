package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.N_DATA_PROTECTION_REGIME;

@Node(labels = N_DATA_PROTECTION_REGIME)
@Getter
@Setter
@NoArgsConstructor
public class DataProtectionRegime extends BaseEntity {
}
