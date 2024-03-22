package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participation_request")
@NamedEntityGraph(
        name = "request-entity-graph-with-requester-event",
        attributeNodes = {
                @NamedAttributeNode("requester"),
                @NamedAttributeNode("event")
        }
)
@NamedEntityGraph(
        name = "request-entity-graph-with-event",
        attributeNodes = {
                @NamedAttributeNode("event")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;


}
