package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatsRequestParams;
import ru.practicum.stats.StatsCreateDto;
import ru.practicum.stats.StatsViewDto;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class StatsClientImpl implements StatsClient {

    private static final String ADD_API_URL = "/hit";
    private static final String GET_API_URL = "/stats";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate;

    @Autowired
    public StatsClientImpl(@Value("${spring.stats-service.uri}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Override
    public void add(StatsCreateDto statsCreateDto) {
        ResponseEntity<Void> response = restTemplate.postForEntity(ADD_API_URL, statsCreateDto, Void.class);
        if (response.getStatusCode() != HttpStatus.CREATED) {
            log.error("Failed to add statistics entry. Status code: {}", response.getStatusCodeValue());
        }
    }

    @Override
    public List<StatsViewDto> get(StatsRequestParams params) {

        String url = GET_API_URL + "?start=" + formatter.format(params.getStart()) +
                "&end=" + formatter.format(params.getEnd()) +
                "&uris=" + String.join(",", params.getUris()) +
                "&unique=" + params.isUnique();

        try {
            ResponseEntity<StatsViewDto[]> response = restTemplate.getForEntity(url, StatsViewDto[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
        } catch (HttpClientErrorException e) {
            log.error("Error during API call: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("Error during API call: {}", e.getMessage());
        }

        return Collections.emptyList();
    }
}