package ru.practicum.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsViewDto {

    String app;

    String uri;

    long hits;
}
