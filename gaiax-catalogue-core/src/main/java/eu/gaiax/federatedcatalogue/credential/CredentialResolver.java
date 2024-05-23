package eu.gaiax.federatedcatalogue.credential;

import eu.gaiax.federatedcatalogue.utils.InvokeService;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class CredentialResolver {

    public JSONObject fetch(String id) {
        return new JSONObject(InvokeService.executeRequest(id, HttpMethod.GET));
    }
}
