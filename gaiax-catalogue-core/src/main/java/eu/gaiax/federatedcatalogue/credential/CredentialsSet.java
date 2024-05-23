package eu.gaiax.federatedcatalogue.credential;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

@RequiredArgsConstructor
public class CredentialsSet implements Iterable<VerifiableCredential> {
    private final List<VerifiableCredential> credentials;

    public Optional<VerifiableCredential> getById(String id) {
        return this.credentials.stream().filter(vc -> vc.getId().equals(id)).findAny();
    }

    public Optional<CredentialSubject> getBySubjectId(String id) {
        for (var vc : this.credentials) {
            for (var cs : vc.getSubjects()) {
                if (cs.getId().equals(id)) {
                    return Optional.of(cs);
                }
            }
        }
        return Optional.empty();
    }

    public Collection<CredentialSubject> getByType(String type) {
        List<CredentialSubject> found = new ArrayList<>();
        for (var vc : this.credentials) {
            for (var cs : vc.getSubjects()) {
                if (cs.hasType(type)) {
                    found.add(cs);
                }
            }
        }
        return found;
    }

    public List<CredentialSubject> getSubjects() {
        List<CredentialSubject> subjects = new ArrayList<>();
        for (var vc : this.credentials) {
            subjects.addAll(vc.getSubjects());
        }
        return subjects;
    }

    @NonNull
    @Override
    public Iterator<VerifiableCredential> iterator() {
        return credentials.iterator();
    }

}
