package ru.practicum.controller.adm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Добавление новой категории");
        CategoryDto createdCategory = categoryService.add(categoryDto);
        log.info("Категория добавлена");
        return createdCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.info("Удаление категории");
        categoryService.delete(catId);
        log.info("Категория удалена");
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId, @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Изменение категории");
        categoryDto.setId(catId);
        CategoryDto updatedCategory = categoryService.update(categoryDto);
        log.info("Данные категории изменены");
        return updatedCategory;
    }
}