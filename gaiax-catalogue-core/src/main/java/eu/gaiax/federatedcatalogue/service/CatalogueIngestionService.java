package eu.gaiax.federatedcatalogue.service;

import eu.gaiax.federatedcatalogue.credential.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CatalogueIngestionService {
    private final CredentialResolver credentialResolverService;
    private final List<AbstractTypedIngestionService> ingestionServices;

    private Map<String, AbstractIngestionService> ingestionServicesByType = new HashMap<>();

    @PostConstruct
    public void init() {
        ingestionServicesByType = ingestionServices
                .stream()
                .collect(Collectors.toMap(AbstractTypedIngestionService::getAcceptedType, Function.identity()));
    }

    public void ingestCredentials(CredentialsSet credentials) {
        for (CredentialSubject credentialSubject : credentials.getSubjects()) {
            ingestionServicesByType
                    .getOrDefault(credentialSubject.getType(), this::typeHasNoIngestion)
                    .ingest(credentialSubject, credentials);
        }
    }

    public void ingestComplianceCredentials(VerifiableCredential complianceVc) {
        var complianceCredentials = new ComplianceCredentialsSet(credentialResolverService, complianceVc).resolved();
        ingestCredentials(complianceCredentials.getResolvedCredentials());
    }

    private void typeHasNoIngestion(CredentialSubject credentialSubject, CredentialsSet credentials) {
        log.info("Ignoring VC type {}", credentialSubject.getType());
    }

}
