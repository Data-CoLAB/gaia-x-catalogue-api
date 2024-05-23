package eu.gaiax.federatedcatalogue.entity.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.*;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node(labels = N_PARTICIPANT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant extends BaseEntity {
    private String type;
    private String did;
    private String credentialSubjectId;
    @Relationship(value = REGISTRATION_NUMBER)
    private Set<RegistrationNumber> registrationNumber = new HashSet<>();
    @Relationship(value = MAINTAINED_BY, direction = INCOMING)
    private ServiceInstance maintainedBy;
    @Relationship(value = TENANT_OWNED_BY, direction = INCOMING)
    private ServiceInstance tenantOwnedBy;
    @Relationship(value = TERMS_AND_CONDITIONS, direction = OUTGOING)
    private TermsAndCondition termsAndCondition;
    @Relationship(value = LEGAL_ADDRESS, direction = OUTGOING)
    private Address legalAddress;
    @Relationship(value = HEADQUARTER_ADDRESS, direction = OUTGOING)
    private Address headQuarterAddress;
    @Relationship(value = PARENT_ORGANIZATION, direction = OUTGOING)
    private Set<Participant> parentOrganization = new HashSet<>();
    @Relationship(value = SUB_ORGANIZATION, direction = OUTGOING)
    private Set<Participant> subOrganization = new HashSet<>();
}
