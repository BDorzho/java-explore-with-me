package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsViewDto;
import ru.practicum.StatsCreateDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
public class StatsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatsService statsService;

    @Autowired
    MockMvc mvc;


    @Test
    public void testAdd() throws Exception {
        // given

        StatsCreateDto statsCreateDto = new StatsCreateDto(1, "app", "/uri", "127.0.0.1", LocalDateTime.now());

        // when // then

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statsCreateDto)))
                .andExpect(status().isOk());

        verify(statsService, times(1)).add(any(StatsCreateDto.class));
    }

    @Test
    public void testGetWithoutUris() throws Exception {
        // given

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedStart = formatter.format(LocalDateTime.now().minusHours(1));
        LocalDateTime parsedStart = LocalDateTime.parse(formattedStart, formatter);
        String formattedEnd = formatter.format(LocalDateTime.now());
        LocalDateTime parsedEnd = LocalDateTime.parse(formattedEnd, formatter);


        List<StatsViewDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(new StatsViewDto("app", "/uri", 3));
        expectedDtoList.add(new StatsViewDto("app", "/uri2", 2));
        // when

        when(statsService.get(parsedStart, parsedEnd, null, false)).thenReturn(expectedDtoList);

        // then

        mvc.perform(get("/stats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", formattedStart)
                        .param("end", formattedEnd)
                        .param("unique", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].app", is("app")))
                .andExpect(jsonPath("$[0].uri", is("/uri")))
                .andExpect(jsonPath("$[0].hits", is(3)))
                .andExpect(jsonPath("$[1].app", is("app")))
                .andExpect(jsonPath("$[1].uri", is("/uri2")))
                .andExpect(jsonPath("$[1].hits", is(2)));

        verify(statsService, times(1)).get(parsedStart, parsedEnd, null, false);
    }

}


