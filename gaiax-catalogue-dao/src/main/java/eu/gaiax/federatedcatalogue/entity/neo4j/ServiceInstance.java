package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.N_SERVICE_INSTANCE;
import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.SERVICE_ACCESS_POINT;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node(labels = N_SERVICE_INSTANCE)
@Getter
@Setter
public class ServiceInstance extends BaseEntity {
    @Relationship(value = SERVICE_ACCESS_POINT, direction = OUTGOING)
    private ServiceAccessPoint accessPoint;
}
