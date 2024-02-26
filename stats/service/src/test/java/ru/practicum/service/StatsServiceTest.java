package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.GetCommonDto;
import ru.practicum.SaveCommonDto;

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

    SaveCommonDto saveCommonDto;

    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(1);


    @Test
    public void testAdd() {
        // given

        saveCommonDto = new SaveCommonDto();
        saveCommonDto.setApp("app");
        saveCommonDto.setUri("/uri");
        saveCommonDto.setIp("192.168.0.1");
        saveCommonDto.setTimeStamp(LocalDateTime.now());

        // when
        service.add(saveCommonDto);

        // then
        List<GetCommonDto> result = service.get(start, end, null, false);
        assertEquals(1, result.size());

    }

    @Test
    public void testGetWithoutUris() {
        // given
        saveCommonDto = new SaveCommonDto();
        saveCommonDto.setApp("app");
        saveCommonDto.setUri("/uri");
        saveCommonDto.setIp("192.168.0.1");
        saveCommonDto.setTimeStamp(LocalDateTime.now());

        service.add(saveCommonDto);

        // when
        List<GetCommonDto> result = service.get(start, end, null, false);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void testGetWithUniqueTrue() {
        // given
        saveCommonDto = new SaveCommonDto();
        saveCommonDto.setApp("app");
        saveCommonDto.setUri("/uri");
        saveCommonDto.setIp("192.168.0.1");
        saveCommonDto.setTimeStamp(LocalDateTime.now());

        service.add(saveCommonDto);

        SaveCommonDto reSaveCommonDto = new SaveCommonDto();
        reSaveCommonDto.setApp("app");
        reSaveCommonDto.setUri("/uri");
        reSaveCommonDto.setIp("192.168.0.1");
        reSaveCommonDto.setTimeStamp(LocalDateTime.now().plusMinutes(30));

        service.add(reSaveCommonDto);

        // when

        List<GetCommonDto> result = service.get(start, end, null, true);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void testGetWithUris() {
        // given
        saveCommonDto = new SaveCommonDto();
        saveCommonDto.setApp("app");
        saveCommonDto.setUri("/uri1");
        saveCommonDto.setIp("192.168.0.1");
        saveCommonDto.setTimeStamp(LocalDateTime.now());

        service.add(saveCommonDto);

        SaveCommonDto reSaveCommonDto = new SaveCommonDto();
        reSaveCommonDto.setApp("app");
        reSaveCommonDto.setUri("/uri2");
        reSaveCommonDto.setIp("192.168.0.12");
        reSaveCommonDto.setTimeStamp(LocalDateTime.now().plusMinutes(30));

        service.add(reSaveCommonDto);

        List<String> uris = Arrays.asList("/uri1", "/uri2");

        // when
        List<GetCommonDto> result = service.get(start, end, uris, false);

        // then
        assertEquals(2, result.size());
    }

}
