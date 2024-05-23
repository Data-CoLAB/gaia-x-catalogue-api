package eu.gaiax.federatedcatalogue.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "GaiaxLLMClient", url = "${gaiax.host.llm}")
public interface LLMClient {

    @PostMapping(value = "/search")
    List<Object> fetchIds(String prompt);
}

