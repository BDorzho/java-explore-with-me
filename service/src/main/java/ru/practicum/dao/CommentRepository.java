package ru.practicum.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "comment-entity-graph")
    List<Comment> findByEventId(long eventId, Pageable pageable);

    @EntityGraph(value = "comment-entity-graph")
    Optional<Comment> findByEventIdAndAuthorId(long eventId, long authorId);
}
