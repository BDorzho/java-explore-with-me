package ru.practicum.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    List<Stats> findByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<Stats> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
