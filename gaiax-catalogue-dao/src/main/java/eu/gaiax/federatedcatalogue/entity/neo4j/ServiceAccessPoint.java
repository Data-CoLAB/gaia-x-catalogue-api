package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.N_SERVICE_ACCESS_POINT;

@Node(labels = N_SERVICE_ACCESS_POINT)
@Getter
@Setter
public class ServiceAccessPoint extends BaseEntity {
    private String version;
    private String port;
    private String protocol;
    private String openAPI;
    private String host;
}
