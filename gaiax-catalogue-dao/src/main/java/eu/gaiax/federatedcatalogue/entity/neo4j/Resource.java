package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.*;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node(labels = N_RESOURCE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource extends BaseEntity {
    private String credentialSubjectId;
    private String type;
    private String description;
    private Boolean containsPII;
    private Date obsoleteDateTime;
    private Date expirationDateTime;
    private String producedBy;
    private String vc;
    private Set<String> policy = new HashSet<>();
    private Set<String> license = new HashSet<>();
    private Set<String> exposedThroughResource = new HashSet<>();
    @Relationship(value = AGGREGATION_OF, direction = OUTGOING)
    private Set<Resource> aggregationResource = new HashSet<>();
    @Relationship(value = COPYRIGHT_OWNED_BY, direction = OUTGOING)
    private Set<Participant> copyRightOwnedBy = new HashSet<>();
    @Relationship(value = MAINTAINED_BY, direction = OUTGOING)
    private Set<Participant> maintainedBy = new HashSet<>();
    @Relationship(value = MANUFACTURED_BY, direction = OUTGOING)
    private Set<Participant> manufacturedBy = new HashSet<>();
    @Relationship(value = OWNED_BY, direction = OUTGOING)
    private Set<Participant> ownedBy = new HashSet<>();
    @Relationship(value = LOCATION, direction = OUTGOING)
    private Set<Address> location = new HashSet<>();
    @Relationship(value = LOCATION_ADDRESS, direction = OUTGOING)
    private Set<Address> locationAddress = new HashSet<>();
    @Relationship(value = HOSTED_ON, direction = INCOMING)
    private ServiceInstance serviceInstance;
}
