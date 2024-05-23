package eu.gaiax.federatedcatalogue.trust;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;

import java.util.Map;

public interface TrustIndexService {

    TrustIndex computeTrustIndex(CredentialSubject credentialSubject, CredentialsSet credentials);

}
