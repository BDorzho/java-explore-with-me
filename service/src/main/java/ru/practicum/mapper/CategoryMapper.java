package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(),
                category.getName());
    }

    public Category toModel(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(),
                categoryDto.getName());
    }
}
