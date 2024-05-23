package eu.gaiax.federatedcatalogue.service;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;

public interface AbstractIngestionService {

    void ingest(CredentialSubject credentialSubject, CredentialsSet credentials);
}
