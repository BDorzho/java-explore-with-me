package ru.practicum.stats.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.StatsViewDto;
import ru.practicum.stats.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.stats.StatsViewDto(s.app, s.uri, COUNT(s.ip) as hits) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND ((:uris) IS NULL OR s.uri IN (:uris)) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    List<StatsViewDto> findByTimestampBetweenAndUriIn(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end,
                                                      @Param("uris") List<String> uris);


    @Query("SELECT new ru.practicum.stats.StatsViewDto(s.app, s.uri, COUNT(DISTINCT s.ip) as hits) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND ((:uris) IS NULL OR s.uri IN (:uris)) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    List<StatsViewDto> findByUniqueIp(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);

}
