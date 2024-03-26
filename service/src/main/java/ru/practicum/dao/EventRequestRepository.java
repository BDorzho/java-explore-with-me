package ru.practicum.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.EventConfirmedRequestsInfo;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    @EntityGraph(value = "request-entity-graph-with-event")
    @Query("SELECT pr FROM ParticipationRequest pr " +
            "JOIN pr.event e " +
            "WHERE e.initiator.id = :initiatorId " +
            "AND e.id = :eventId")
    List<ParticipationRequest> findRequestsByInitiatorIdAndEventId(@Param("initiatorId") Long initiatorId, @Param("eventId") Long eventId);

    @EntityGraph(value = "request-entity-graph-with-requester-event")
    List<ParticipationRequest> findByRequesterId(Long id);

    @EntityGraph(value = "request-entity-graph-with-requester-event")
    Optional<ParticipationRequest> findByIdAndRequesterId(Long requestId, Long id);


    @Query("SELECT new ru.practicum.dto.EventConfirmedRequestsInfo(pr.event.id, COUNT(pr.id)) " +
            "FROM ParticipationRequest pr " +
            "WHERE pr.event.id IN :eventIds AND pr.status = 'CONFIRMED' " +
            "GROUP BY pr.event.id")
    List<EventConfirmedRequestsInfo> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);

    boolean existsByRequesterIdAndEventIdAndStatus(long requesterId, long eventId, RequestStatus requestStatus);
}
