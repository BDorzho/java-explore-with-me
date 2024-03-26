package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateDto {

    private Long id;

    private long eventId;

    private long author;

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 3, max = 2000, message = "Комментарий к событию должен содержать от 3 до 2000 символов.")
    private String text;

}

