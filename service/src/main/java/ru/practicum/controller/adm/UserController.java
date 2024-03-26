package ru.practicum.controller.adm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/admin/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> get(@RequestParam(required = false) List<Long> ids,
                             @RequestParam(defaultValue = "0") int from,
                             @RequestParam(defaultValue = "10") int size) {
        log.info("Получение информации о пользователях");
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Информация получена");
        return service.get(ids, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.info("Добавление нового пользователя: {}", userDto);
        UserDto createUser = service.add(userDto);
        log.info("Пользователь зарегистрирован");
        return createUser;
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") long userId) {
        log.info("Удаление пользователя с идентификатором: {}", userId);
        service.delete(userId);
        log.info("Пользователь с идентификатором {} удален", userId);
    }
}
