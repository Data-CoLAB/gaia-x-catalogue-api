package eu.gaiax.federatedcatalogue.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "GaiaxCESClient", url = "${gaiax.host.ces}")
public interface CESClient {

    /**
     * Fetch the credentials event from the Gaia-x CES server.
     *
     * @param lastReceivedID - Indicates the last received ID
     * @param page           - Indicates the page
     * @param size           - Indicates the size
     * @return ResponseEntity
     */
    @GetMapping(value = "/credentials-events")
    List<Object> fetchCredentials(@RequestParam("lastReceivedID") String lastReceivedID, @RequestParam(value = "page", defaultValue = "0") Long page, @RequestParam(value = "size", defaultValue = "20") Long size);
}
