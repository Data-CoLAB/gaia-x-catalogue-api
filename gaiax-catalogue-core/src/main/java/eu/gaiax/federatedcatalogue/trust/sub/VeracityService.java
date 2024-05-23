package eu.gaiax.federatedcatalogue.trust.sub;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.credential.VerifiableCredential;
import eu.gaiax.federatedcatalogue.service.registry.RegistryService;
import eu.gaiax.federatedcatalogue.trust.TrustIndex;
import eu.gaiax.federatedcatalogue.utils.InvokeService;
import foundation.identity.did.VerificationMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uniresolver.UniResolver;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class VeracityService implements TrustSubIndexService {
    private final UniResolver uniResolver;
    private final RegistryService registryService;

    @Override
    public TrustIndex.SubIndex getSubIndex() {
        return TrustIndex.SubIndex.VERACITY;
    }

    @Override
    public Double compute(CredentialSubject credentialSubject, CredentialsSet credentials) {
        VerifiableCredential vc = credentialSubject.getVerifiableCredential();
        return compute(vc.getString("issuer"), vc.getString("proof", "verificationMethod"));
    }

    public Double compute(String issuer, String verificationMethod) {
        try {
            var doc = this.uniResolver.resolve(issuer).getDidDocument();
            VerificationMethod method = doc.getVerificationMethods()
                    .stream()
                    .filter(v -> verificationMethod.equals(v.getId().toString()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("Verification method " + verificationMethod + " not found for issuer " + issuer));
            return compute(method);
        } catch (Exception e) {
            log.error("Compute veracity failed for {} {}", issuer, verificationMethod, e);
            return 0d;
        }
    }

    public Double compute(VerificationMethod method) {
        String certChainUrl = method.getPublicKeyJwk().get("x5u").toString();
        String certChain = InvokeService.executeRequest(certChainUrl, HttpMethod.GET);
        if (!registryService.isValidCertificateChain(certChain)) {
            return 0d;
        }
        int length = StringUtils.countOccurrencesOf(certChain, "-----BEGIN CERTIFICATE-----");
        if (length == 0) {
            return 0d;
        }
        return Math.pow(.9, length - 1);
    }

}
