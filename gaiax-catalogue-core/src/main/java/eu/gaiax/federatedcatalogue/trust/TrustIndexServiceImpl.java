package eu.gaiax.federatedcatalogue.trust;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.trust.sub.TrustSubIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrustIndexServiceImpl implements TrustIndexService {

    private final List<TrustSubIndexService> subIndexServices;

    @Override
    public TrustIndex computeTrustIndex(CredentialSubject credentialSubject, CredentialsSet credentials) {
        Map<TrustIndex.SubIndex, Double> subIndexes = new HashMap<>();
        for (var service : subIndexServices) {
            var value = service.compute(credentialSubject, credentials);
            if (value != null) {
                subIndexes.put(service.getSubIndex(), value);
                log.info("{} {} = {}", credentialSubject.getId(), service.getSubIndex().name(), value);
            }
        }
        double trust = subIndexes
                .values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0d);
        log.info("{} trust = {}", credentialSubject.getId(), trust);
        return new TrustIndex(subIndexes, trust);
    }

}
