package eu.gaiax.federatedcatalogue.entity.neo4j;

import eu.gaiax.federatedcatalogue.neo4j.GaiaxEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;
import java.util.Map;

@NodeEntity
@Getter
@Setter
public class Node extends BaseEntity implements GaiaxEntity {
    @Property("label")
    private List<String> labels;
    @Property("properties")
    private Map<String, String> properties;
}
