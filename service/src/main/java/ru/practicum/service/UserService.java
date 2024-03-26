package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> get(List<Long> ids, Pageable pageable);

    UserDto add(UserDto userDto);

    void delete(Long userId);
}
