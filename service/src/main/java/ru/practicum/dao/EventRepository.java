package ru.practicum.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;


import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @EntityGraph(value = "event-entity-graph")
    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id IN :eventIds")
    @EntityGraph(value = "event-entity-graph")
    List<Event> findAllByIds(List<Long> eventIds);

    @Query("SELECT e FROM Event e WHERE e.id = :eventId AND e.state = :state")
    @EntityGraph("event-entity-graph")
    Optional<Event> findPublishedEvents(@Param("eventId") Long eventId, @Param("state") EventState state);
}
