package eu.gaiax.federatedcatalogue.service.neo4j.serviceoffering;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsensesolutions.java.commons.sort.SortType;
import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.entity.neo4j.*;
import eu.gaiax.federatedcatalogue.exception.EntityNotFoundException;
import eu.gaiax.federatedcatalogue.model.request.RecordFilter;
import eu.gaiax.federatedcatalogue.model.response.CataloguePage;
import eu.gaiax.federatedcatalogue.model.response.ServiceDetailResponse;
import eu.gaiax.federatedcatalogue.model.response.ServiceListResponse;
import eu.gaiax.federatedcatalogue.repository.neo4j.ServiceOfferRepository;
import eu.gaiax.federatedcatalogue.service.AbstractTypedIngestionService;
import eu.gaiax.federatedcatalogue.service.neo4j.JsonProcessorService;
import eu.gaiax.federatedcatalogue.service.neo4j.labellevel.LabelLevelService;
import eu.gaiax.federatedcatalogue.service.neo4j.model.resource.ResourceDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.serviceoffering.DataAccountExportDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.serviceoffering.ServiceOfferDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.serviceoffering.TermsAndConditionDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.participant.AddressService;
import eu.gaiax.federatedcatalogue.service.neo4j.participant.ParticipantService;
import eu.gaiax.federatedcatalogue.service.neo4j.participant.TermsAndConditionService;
import eu.gaiax.federatedcatalogue.service.neo4j.resource.ResourceService;
import eu.gaiax.federatedcatalogue.service.neo4j.search.OperationService;
import eu.gaiax.federatedcatalogue.trust.TrustIndex;
import eu.gaiax.federatedcatalogue.trust.TrustIndexService;
import eu.gaiax.federatedcatalogue.utils.InvokeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceOfferService implements AbstractTypedIngestionService {
    private final ObjectMapper mapper;
    private final ParticipantService participantService;
    private final ServiceOfferRepository serviceOfferRepository;
    private final TermsAndConditionService termsAndConditionService;
    private final ResourceService resourceService;
    private final TrustIndexService trustIndexService;
    private final JsonProcessorService processorService;
    private final AddressService addressService;
    private final LabelLevelService labelLevelService;
    private final OperationService operationService;

    public ServiceOfferDTO ingestServiceOffer(CredentialSubject credentialSubject, CredentialsSet credentials) {
        if (Objects.isNull(credentialSubject) || !credentialSubject.hasType(GX_SERVICE_OFFERING)) {
            return null;
        }
        String participantUrl = getId(credentialSubject.getObject(GX_PROVIDED_BY));
        ServiceOfferDTO offer = new ServiceOfferDTO();
        offer.setCredentialSubjectId(credentialSubject.getId());
        offer.setName(credentialSubject.getString(GX_NAME));
        offer.setDescription(credentialSubject.getString(GX_DESCRIPTION));
        offer.setTnc(this.processorService.processForTnc(credentialSubject.getObject(GX_TERMS_AND_CONDITIONS)));
        offer.setLocations(findLocations(credentialSubject, credentials));
        JSONObject dataAccountExport = credentialSubject.getObject(GX_DATA_ACCOUNT_EXPORT);
        JSONArray formatTypes = getAsArray(dataAccountExport, GX_FORMAT_TYPE);
        HashSet<String> formats = new HashSet<>();
        this.processorService.processString(formatTypes, formats);
        offer.setDataAccountExport(new DataAccountExportDTO(dataAccountExport.getString(GX_REQUEST_TYPE), dataAccountExport.getString(GX_ACCESS_TYPE), formats));
        this.processorService.processString(credentialSubject.getArray(GX_DATA_PROTECTION_REGIME), offer.getDataProtectionRegime());
        this.processorService.processString(credentialSubject.getArray(GX_POLICY), offer.getPolicy());
        credentials.getBySubjectId(participantUrl).filter(cs -> cs.hasType(GX_LEGAL_PARTICIPANT)).ifPresent(cs -> {
            offer.setProvidedBy(this.participantService.createParticipantDto(cs, credentials));
        });
        this.processOnDependsService(credentials, offer, credentialSubject.getArray(GX_DEPENDS_ON));
        this.processOnResource(credentials, offer, credentialSubject.getArray(GX_AGGREGATION_OF));
        offer.setTrust(this.trustIndexService.computeTrustIndex(credentialSubject, credentials));
        offer.setLabelLevel(this.labelLevelService.getLabelLevel(credentialSubject));
        this.create(offer);
        return offer;
    }

    private void processOnResource(CredentialsSet credentials, ServiceOfferDTO offer, JSONArray aggregationResource) {
        if (Objects.isNull(aggregationResource)) {
            return;
        }

        aggregationResource.forEach(r -> {
            JSONObject aggs = (JSONObject) r;
            String id = getId(aggs);
            var resourceOpt = credentials.getBySubjectId(id);
            if (resourceOpt.isPresent()) {
                ResourceDTO resource = this.resourceService.ingestResource(resourceOpt.get(), credentials);
                if (Objects.nonNull(resource)) {
                    offer.getAggregationOf().add(resource);
                }
            }
        });
    }

    private void processOnDependsService(CredentialsSet credentials, ServiceOfferDTO offer, JSONArray dependsOnServices) {
        if (Objects.isNull(dependsOnServices)) {
            return;
        }
        dependsOnServices.forEach(s -> {
            JSONObject service = (JSONObject) s;
            var dependedService = credentials.getBySubjectId(getId(service));
            if (dependedService.isPresent()) {
                ServiceOfferDTO dto = this.ingestServiceOffer(dependedService.get(), credentials);
                if (Objects.nonNull(dto)) {
                    offer.getDependsOn().add(dto);
                }
            }
        });
    }

    private Set<Address> findLocations(CredentialSubject credentialSubject, CredentialsSet credentials) {
        Set<Address> addresses = new HashSet<>();
        addresses.addAll(findLocations(findSubject(credentials, credentialSubject, "gx:maintainedBy")));
        addresses.addAll(findLocations(findSubject(credentials, credentialSubject, "gx:providedBy")));
        addresses.addAll(findLocations(findSubject(credentials, credentialSubject, "gx:producedBy")));
        return addresses;
    }

    private CredentialSubject findSubject(CredentialsSet credentials, CredentialSubject credentialSubject, String key) {
        return credentials.getBySubjectId(credentialSubject.getVerifiableCredential().getString(key)).orElseGet(CredentialSubject::new);
    }

    private Set<Address> findLocations(CredentialSubject participant) {
        Set<Address> addresses = new HashSet<>();
        addresses.add(Address.builder()
                .countrySubdivisionCode(participant.getString(GX_HEADQUARTER_ADDRESS, GX_COUNTRY_SUBDIVISION_CODE))
                .build());
        addresses.add(Address.builder()
                .countrySubdivisionCode(participant.getString(GX_LEGAL_ADDRESS, GX_COUNTRY_SUBDIVISION_CODE))
                .build());
        addresses.removeIf(a -> a.getCountrySubdivisionCode().isEmpty());
        return addresses;
    }

    private ServiceOffer create(ServiceOfferDTO dto) {
        ServiceOffer offer = this.create(
                dto.getCredentialSubjectId(),
                dto.getDescription(),
                dto.getName(),
                dto.getPolicy(),
                dto.getTnc(),
                dto.getLabelLevel(),
                dto.getDataAccountExport(),
                dto.getDataProtectionRegime(),
                this.participantService.create(dto.getProvidedBy()),
                dto.getTrust().subIndexes().get(TrustIndex.SubIndex.VERACITY),
                dto.getTrust().value(),
                dto.getTrust().subIndexes().get(TrustIndex.SubIndex.TRANSPARENCY),
                dto.getLocations()
        );
        for (ServiceOfferDTO dependsOn : dto.getDependsOn()) {
            if (CollectionUtils.isEmpty(offer.getDependedServices())) {
                offer.setDependedServices(new HashSet<>());
            }
            offer.getDependedServices().add(this.create(dependsOn.getCredentialSubjectId(), dependsOn.getDescription(), dependsOn.getName(), dependsOn.getPolicy(), dependsOn.getTnc(), dependsOn.getLabelLevel(),
                    dependsOn.getDataAccountExport(), dependsOn.getDataProtectionRegime(), this.participantService.create(dependsOn.getProvidedBy()),
                    dependsOn.getTrust().subIndexes().get(TrustIndex.SubIndex.VERACITY), dependsOn.getTrust().value(), dependsOn.getTrust().subIndexes().get(TrustIndex.SubIndex.TRANSPARENCY), dependsOn.getLocations()));
        }

        for (ResourceDTO resource : dto.getAggregationOf()) {
            if (CollectionUtils.isEmpty(offer.getResources())) {
                offer.setResources(new HashSet<>());
            }
            offer.getResources().add(this.resourceService.create(resource));
        }
        return this.serviceOfferRepository.save(offer);
    }

    private ServiceOffer create(String credentialSubjectId, String description, String name, Set<String> policyUrl, TermsAndConditionDTO tnc, String labelLevel,
                                DataAccountExportDTO dataAccountExport, Set<String> dataProtectionRegimes, Participant providedBy,
                                Double veracity, Double trustIndex, Double transparency, Set<Address> locations) {
        ServiceOffer serviceOffer = this.serviceOfferRepository.getByCredentialSubjectId(credentialSubjectId);
        UUID id = serviceOffer == null ? null : serviceOffer.getId();
        Set<DataProtectionRegime> regimes = dataProtectionRegimes.stream().map(regime -> {
            DataProtectionRegime protectionRegime = new DataProtectionRegime();
            protectionRegime.setName(regime);
            return protectionRegime;
        }).collect(Collectors.toSet());
        serviceOffer = ServiceOffer.builder()
                .credentialSubjectId(credentialSubjectId)
                .policy(policyUrl)
                .description(description)
                .labelLevel(labelLevel)
                .protectionRegime(CollectionUtils.isEmpty(regimes) ? null : regimes)
                .dataAccountExport(DataAccountExport.builder().accessType(dataAccountExport.accessType()).formatType(dataAccountExport.formatType()).requestType(dataAccountExport.requestType()).build())
                .termsAndCondition(this.termsAndConditionService.create(tnc.hash()))
                .participant(providedBy)
                .veracity(veracity)
                .trustIndex(trustIndex)
                .transparency(transparency)
                .locations(locations)
                .vc(InvokeService.executeRequest(credentialSubjectId, HttpMethod.GET))
                .build();
        serviceOffer.setId(id);
        serviceOffer.setName(name);
        serviceOffer.setCreatedAt(new Date());
        return this.serviceOfferRepository.save(serviceOffer);
    }

    public CataloguePage<ServiceListResponse> getDefault(eu.gaiax.federatedcatalogue.model.request.Pageable pageable) {
        log.info("Getting page of ServiceListResponse");

        RecordFilter filter = new RecordFilter();
        filter.setPage((int) pageable.getPage());
        filter.setSize((int) pageable.getSize());

        com.smartsensesolutions.java.commons.sort.Sort sort = new com.smartsensesolutions.java.commons.sort.Sort();
        sort.setColumn("createdAt");
        sort.setSortType(SortType.DESC);

        filter.setSort(List.of(sort));

        return getSearchData(filter);
    }

    public CataloguePage<ServiceListResponse> getSearchData(RecordFilter filter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // Default sorting if none provided
        if (filter.getSort() != null) {
            List<Sort.Order> orders = new ArrayList<>();
            for (com.smartsensesolutions.java.commons.sort.Sort sorts : filter.getSort()) {
                Sort.Direction direction = sorts.getSortType().getValue().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Sort.Order order = new Sort.Order(direction, sorts.getColumn());
                orders.add(order);
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<ServiceOffer> data = operationService.getSearchDetails(filter, pageable, sort);

        List<ServiceListResponse> responses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(data.getContent())) {
            for (ServiceOffer serviceOffer : data.getContent()) {
                this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ServiceListResponse serviceResponse = this.mapper.convertValue(serviceOffer, ServiceListResponse.class);
                if (serviceOffer.getParticipant() != null) {
                    serviceResponse.setProvidedBy(serviceOffer.getParticipant().getName());
                }
                responses.add(serviceResponse);
            }
        }
        return CataloguePage.of(responses, data);
    }

    public ServiceDetailResponse getServiceDetailsById(UUID id) {
        ServiceOffer serviceOffer = this.serviceOfferRepository.findById(id).orElse(null);
        if (serviceOffer == null) {
            throw new EntityNotFoundException("service.not.found");
        }

        return this.mapper.convertValue(serviceOffer, ServiceDetailResponse.class);
    }

    @Override
    public void ingest(CredentialSubject credentialSubject, CredentialsSet credentials) {
        ingestServiceOffer(credentialSubject, credentials);
    }

    @Override
    public String getAcceptedType() {
        return GX_SERVICE_OFFERING;
    }
}
