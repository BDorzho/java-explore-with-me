package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
import ru.practicum.mapper.UserMapper;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.UserService;
import ru.practicum.validation.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> get(List<Long> ids, Pageable pageable) {
        Page<User> usersPage;
        if (ids != null && !ids.isEmpty()) {
            usersPage = userRepository.findAllByIdIn(ids, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.getContent().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = mapper.toModel(userDto);
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);
    }
}
