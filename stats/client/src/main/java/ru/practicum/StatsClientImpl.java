package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.StatsRequestParams;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatsClientImpl implements StatsClient {

    private final RestTemplate rest;
    private final String serverUrl;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClientImpl(RestTemplate rest, @Value("http://localhost:9090") String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    @Override
    public void add(StatsCreateDto statsCreateDto) {
        rest.postForObject(serverUrl + "/hit", statsCreateDto, Void.class);
    }

    @Override
    public List<StatsViewDto> get(StatsRequestParams params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(serverUrl + "/stats")
                .queryParam("start", params.getStart().format(formatter))
                .queryParam("end", params.getEnd().format(formatter));

        if (params.getUris() != null && !params.getUris().isEmpty()) {
            builder.queryParam("uris", String.join(",", params.getUris()));
        }

        builder.queryParam("unique", params.isUnique());

        ResponseEntity<List<StatsViewDto>> responseEntity;
        try {
            responseEntity = rest.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
            if (responseEntity.getStatusCodeValue() == 200) {
                return responseEntity.getBody();
            }
        } catch (Exception e) {
            log.error("При выполнении вызова API произошла ошибка: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
