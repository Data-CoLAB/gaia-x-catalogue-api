package eu.gaiax.federatedcatalogue.service.neo4j.resource;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.entity.neo4j.Address;
import eu.gaiax.federatedcatalogue.entity.neo4j.Participant;
import eu.gaiax.federatedcatalogue.entity.neo4j.Resource;
import eu.gaiax.federatedcatalogue.repository.neo4j.ResourceRepository;
import eu.gaiax.federatedcatalogue.service.neo4j.JsonProcessorService;
import eu.gaiax.federatedcatalogue.service.neo4j.model.participant.ParticipantDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.resource.LocationDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.resource.ResourceDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.participant.AddressService;
import eu.gaiax.federatedcatalogue.service.neo4j.participant.ParticipantService;
import eu.gaiax.federatedcatalogue.utils.InvokeService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.*;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ParticipantService participantService;
    private final AddressService addressService;
    private final JsonProcessorService processorService;

    public ResourceDTO ingestResource(CredentialSubject credentialSubject, CredentialsSet credentials) {
        if (Objects.isNull(credentialSubject) || !credentialSubject.hasAnyType(GX_PHYSICAL_RESOURCE, GX_VIRTUAL_SOFTWARE_RESOURCE, GX_VIRTUAL_DATA_RESOURCE)) {
            return null;
        }
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setType(credentialSubject.getType());
        resourceDTO.setCredentialSubjectId(credentialSubject.getId());
        resourceDTO.setName(credentialSubject.getString(GX_NAME));
        resourceDTO.setDescription(credentialSubject.getString(GX_DESCRIPTION));
        resourceDTO.setContainsPII(credentialSubject.getBoolean(GX_CONTAINS_PII));

        this.processorService.processParticipant(credentialSubject.getArray(GX_MAINTAINED_BY), resourceDTO.getMaintainedBy(), credentials);
        this.processorService.processParticipant(credentialSubject.getArray(GX_OWNED_BY), resourceDTO.getOwnedBy(), credentials);
        this.processorService.processParticipant(credentialSubject.getArray(GX_MANUFACTURED_BY), resourceDTO.getManufacturedBy(), credentials);
        this.processorService.processParticipant(credentialSubject.getArray(GX_COPYRIGHT_OWNED_BY), resourceDTO.getCopyrightOwnedBy(), credentials);

        this.processorService.processLocation(credentialSubject.getArray(GX_LOCATION_ADDRESS), resourceDTO.getLocationAddress());
        this.processorService.processLocation(credentialSubject.getArray(GX_LOCATION), resourceDTO.getLocation());

        this.processorService.processString(credentialSubject.getArray(GX_LICENSE), resourceDTO.getLicense());
        this.processorService.processString(credentialSubject.getArray(GX_POLICY), resourceDTO.getPolicies());
        this.processorService.processString(credentialSubject.getArray(GX_EXPOSED_THROUGH), resourceDTO.getExposedThrough());

        JSONObject producedBy = credentialSubject.getObject(GX_PRODUCED_BY);
        if (Objects.nonNull(producedBy)) {
            resourceDTO.setProducedBy(getId(producedBy));
        }
        JSONArray aggregationResource = credentialSubject.getArray(GX_AGGREGATION_OF);
        if (Objects.nonNull(aggregationResource)) {
            aggregationResource.forEach(ag -> {
                JSONObject aggregateResource = (JSONObject) ag;
                var aggVc = credentials.getBySubjectId(getId(aggregateResource));
                if (aggVc.isPresent()) {
                    ResourceDTO resource = this.ingestResource(aggVc.get(), credentials);
                    if (Objects.nonNull(resource)) {
                        resourceDTO.getAggregationOf().add(resource);
                    }
                }
            });
        }
        this.create(resourceDTO);
        return resourceDTO;
    }

    public Resource create(ResourceDTO dto) {
        return this.create(dto.getCredentialSubjectId(), dto.getType(), dto.getName(), dto.getDescription(), dto.getPolicies(),
                dto.getLicense(), this.processOnAggregateResource(dto.getAggregationOf()),
                dto.getExposedThrough(), dto.getProducedBy(),
                this.processOnParticipant(dto.getMaintainedBy()), this.processOnParticipant(dto.getOwnedBy()), this.processOnParticipant(dto.getManufacturedBy()), this.processOnParticipant(dto.getCopyrightOwnedBy()),
                this.processOnLocation(dto.getLocationAddress()), this.processOnLocation(dto.getLocation()),
                dto.getContainsPII());
    }

    private Resource create(String credentialSubjectId, String type, String name, String description, Set<String> policy,
                            Set<String> licenses, Set<Resource> aggregateResources,
                            Set<String> exposedDataResource, String producedBy,
                            Set<Participant> maintainer, Set<Participant> owner, Set<Participant> manufacturer, Set<Participant> copyrightOwnedBy,
                            Set<Address> address, Set<Address> location,
                            boolean containsPII) {
        Resource resource = this.resourceRepository.getByCredentialSubjectId(credentialSubjectId);
        if (Objects.nonNull(resource)) {
            return resource;
        }
        resource = Resource.builder()
                .credentialSubjectId(credentialSubjectId)
                .type(type)
                .description(description)
                .policy(policy)
                .containsPII(containsPII)
                .producedBy(producedBy)
                .license(licenses)
                .exposedThroughResource(exposedDataResource)
                .aggregationResource(aggregateResources)
                .copyRightOwnedBy(copyrightOwnedBy)
                .maintainedBy(maintainer)
                .manufacturedBy(manufacturer)
                .ownedBy(owner)
                .location(location)
                .locationAddress(address)
                .vc(InvokeService.executeRequest(credentialSubjectId, HttpMethod.GET))
                .build();
        resource.setName(name);
        resource.setCreatedAt(new Date());
        return this.resourceRepository.save(resource);
    }

    private Set<Participant> processOnParticipant(Set<ParticipantDTO> participants) {
        if (CollectionUtils.isEmpty(participants)) {
            return Collections.emptySet();
        }
        return participants.stream().map(this.participantService::create).collect(Collectors.toSet());
    }

    private Set<Address> processOnLocation(Set<LocationDTO> location) {
        if (CollectionUtils.isEmpty(location)) {
            return Collections.emptySet();
        }
        return location.stream().map(this.addressService::create).collect(Collectors.toSet());
    }

    private Set<Resource> processOnAggregateResource(Set<ResourceDTO> resources) {
        if (CollectionUtils.isEmpty(resources)) {
            return Collections.emptySet();
        }
        return resources.stream().map(this::create).collect(Collectors.toSet());
    }
}
