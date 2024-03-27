package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(CategoryDto categoryDto);

    void delete(long catId);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(long catId);
}
