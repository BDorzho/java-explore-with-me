package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dto.CategoryDto;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.service.CategoryService;
import ru.practicum.validation.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final CategoryMapper mapper;

    @Override
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = mapper.toModel(categoryDto);
        return mapper.toDto(repository.save(category));
    }

    @Override
    public void delete(long catId) {
        repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        repository.deleteById(catId);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        repository.findById(categoryDto.getId())
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryDto.getId() + " was not found"));
        Category category = mapper.toModel(categoryDto);
        return mapper.toDto(repository.save(category));
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        List<Category> categories = repository.findAll(pageable).getContent();
        return categories.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(long catId) {
        return mapper.toDto(repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found")));
    }
}
