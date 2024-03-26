package ru.practicum.dto;

import lombok.*;

import javax.persistence.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Column(name = "lat")
    private float lat;
    @Column(name = "lon")
    private float lon;
}
