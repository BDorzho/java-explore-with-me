package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 1, max = 50, message = "Название категории должно содержать от 1 до 50 символов.")
    private String name;
}
