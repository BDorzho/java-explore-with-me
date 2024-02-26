package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private final RestTemplate rest;
    private final String serverUrl = "${stats-service.url}";

    @Autowired
    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    public void addStats(SaveCommonDto saveCommonDto) {
        rest.postForObject(serverUrl + "/hit", saveCommonDto, Void.class);
    }

    public List<GetCommonDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(serverUrl + "/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter));

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }

        builder.queryParam("unique", unique);

        ResponseEntity<List<GetCommonDto>> responseEntity = rest.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }
}
