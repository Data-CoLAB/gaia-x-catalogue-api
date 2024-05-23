package eu.gaiax.federatedcatalogue.utils;

import eu.gaiax.federatedcatalogue.utils.exception.InvalidServiceOfferingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvokeService {

    public static final String ERROR_MESSAGE = "PolicyValidationService(configurePostWebClient): Invalid Service offering request {} with statuscode {}";

    public static String executeRequest(String url, HttpMethod method) {
        return executeRequest(url, method, null);
    }

    public static String executeRequest(String url, HttpMethod method, Object body) {
        return executeRequest(url, method, body, null);
    }

    public static String executeRequest(String url, HttpMethod method, Object body, MultiValueMap<String, String> queryParams) {
        WebClient.RequestBodyUriSpec webClient = WebClient.create().method(method);
        webClient.uri(url);
        if (!CollectionUtils.isEmpty(queryParams)) {
            webClient.uri(url, u -> u.queryParams(queryParams).build());
        }
        if (Objects.nonNull(body)) {
            webClient.bodyValue(body);
        }
        return webClient.retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, error -> {
                    log.error(ERROR_MESSAGE, url, HttpStatus.FORBIDDEN.value());
                    return error.bodyToMono(String.class).map(InvalidServiceOfferingException::new);
                }).onStatus(HttpStatus.BAD_REQUEST::equals, error -> {
                    log.error(ERROR_MESSAGE, url, HttpStatus.FORBIDDEN.value());
                    return error.bodyToMono(String.class).map(InvalidServiceOfferingException::new);
                }).onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, error -> {
                    log.error(ERROR_MESSAGE, url, HttpStatus.FORBIDDEN.value());
                    return error.bodyToMono(String.class).map(InvalidServiceOfferingException::new);
                }).bodyToMono(String.class)
                .block(Duration.ofSeconds(1));
    }
}
