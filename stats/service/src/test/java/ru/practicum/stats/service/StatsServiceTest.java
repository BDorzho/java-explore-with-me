package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.stats.StatsViewDto;
import ru.practicum.stats.StatsCreateDto;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceTest {

    private final StatsService service;

    StatsCreateDto statsCreateDto;

    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(1);


    @Test
    public void testAdd() {
        // given

        statsCreateDto = new StatsCreateDto();
        statsCreateDto.setApp("app");
        statsCreateDto.setUri("/uri");
        statsCreateDto.setIp("192.168.0.1");
        statsCreateDto.setTimeStamp(LocalDateTime.now());

        // when
        service.add(statsCreateDto);

        // then
        List<StatsViewDto> result = service.get(start, end, null, false);
        assertEquals(1, result.size());

    }

    @Test
    public void testGetWithoutUris() {
        // given
        statsCreateDto = new StatsCreateDto();
        statsCreateDto.setApp("app");
        statsCreateDto.setUri("/uri");
        statsCreateDto.setIp("192.168.0.1");
        statsCreateDto.setTimeStamp(LocalDateTime.now());

        service.add(statsCreateDto);

        // when
        List<StatsViewDto> result = service.get(start, end, null, false);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void testGetWithUniqueTrue() {
        // given
        statsCreateDto = new StatsCreateDto();
        statsCreateDto.setApp("app");
        statsCreateDto.setUri("/uri");
        statsCreateDto.setIp("192.168.0.1");
        statsCreateDto.setTimeStamp(LocalDateTime.now());

        service.add(statsCreateDto);

        StatsCreateDto reStatsCreateDto = new StatsCreateDto();
        reStatsCreateDto.setApp("app");
        reStatsCreateDto.setUri("/uri");
        reStatsCreateDto.setIp("192.168.0.1");
        reStatsCreateDto.setTimeStamp(LocalDateTime.now().plusMinutes(30));

        service.add(reStatsCreateDto);

        // when

        List<StatsViewDto> result = service.get(start, end, null, true);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void testGetWithUris() {
        // given
        statsCreateDto = new StatsCreateDto();
        statsCreateDto.setApp("app");
        statsCreateDto.setUri("/uri1");
        statsCreateDto.setIp("192.168.0.1");
        statsCreateDto.setTimeStamp(LocalDateTime.now());

        service.add(statsCreateDto);

        StatsCreateDto reStatsCreateDto = new StatsCreateDto();
        reStatsCreateDto.setApp("app");
        reStatsCreateDto.setUri("/uri2");
        reStatsCreateDto.setIp("192.168.0.12");
        reStatsCreateDto.setTimeStamp(LocalDateTime.now().plusMinutes(30));

        service.add(reStatsCreateDto);

        List<String> uris = Arrays.asList("/uri1", "/uri2");

        // when
        List<StatsViewDto> result = service.get(start, end, uris, false);

        // then
        assertEquals(2, result.size());
    }

}
