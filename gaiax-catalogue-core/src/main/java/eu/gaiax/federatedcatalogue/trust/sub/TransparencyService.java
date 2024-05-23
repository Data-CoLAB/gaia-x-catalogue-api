package eu.gaiax.federatedcatalogue.trust.sub;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.service.registry.RegistryService;
import eu.gaiax.federatedcatalogue.trust.TrustIndex;
import eu.gaiax.federatedcatalogue.utils.JSONPathValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransparencyService implements TrustSubIndexService {
    private final RegistryService registryService;

    @Override
    public TrustIndex.SubIndex getSubIndex() {
        return TrustIndex.SubIndex.TRANSPARENCY;
    }

    @Override
    public Double compute(CredentialSubject credentialSubject, CredentialsSet credentials) {
        var shapes = registryService.getTrustFrameworkShapes();
        AtomicInteger total = new AtomicInteger();
        AtomicInteger notEmpty = new AtomicInteger();
        AtomicInteger mandatory = new AtomicInteger();
        credentialSubject.stream().filter(this::isNotJSONLdPath).forEach(prop -> {
            total.addAndGet(1);
            if (!prop.isEmpty()) {
                notEmpty.addAndGet(1);
            }
            if (shapes.isMandatory(credentialSubject.getType(), prop.path())) {
                mandatory.addAndGet(1);
            }
        });
        return (total.doubleValue() + notEmpty.doubleValue()) / Math.max(1d, mandatory.doubleValue());
    }

    private boolean isNotJSONLdPath(JSONPathValue pathValue) {
        String[] path = pathValue.path();
        return !(path.length == 1 && ("@type".equals(path[0]) || "@id".equals(path[0])));
    }

}
