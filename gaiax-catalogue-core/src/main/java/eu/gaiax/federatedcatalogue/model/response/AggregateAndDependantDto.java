package eu.gaiax.federatedcatalogue.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AggregateAndDependantDto {

    private UUID id;

    private String name;

    private String credentialSubjectId;
}
