package ru.practicum.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.dto.EventFilterDto;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class EventSpecification implements Specification<Event> {


    private final EventFilterDto filter;


    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new ArrayList<>();

        getInitiatorPredicate(root, criteriaBuilder).ifPresent(predicates::add);
        getStatePredicate(root).ifPresent(predicates::add);
        getCategoryPredicate(root, criteriaBuilder).ifPresent(predicates::add);
        getRangeStartPredicate(root, criteriaBuilder).ifPresent(predicates::add);
        getRangeEndPredicate(root, criteriaBuilder).ifPresent(predicates::add);
        getSearchTextPredicate(root, criteriaBuilder).ifPresent(predicates::add);
        getPaidPredicate(root, criteriaBuilder).ifPresent(predicates::add);
        getOnlyAvailablePredicate(root, criteriaBuilder).ifPresent(predicates::add);


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Optional<Predicate> getInitiatorPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(filter.getUsers())
                .map(users -> criteriaBuilder.in(root.get("initiator").get("id")).value(users));
    }

    private Optional<Predicate> getStatePredicate(Root<Event> root) {
        return Optional.ofNullable(filter.getStates())
                .filter(states -> !states.isEmpty())
                .map(states -> root.get("state").in(states));
    }

    private Optional<Predicate> getCategoryPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(filter.getCategories())
                .filter(categories -> !categories.isEmpty())
                .map(categories -> criteriaBuilder.in(root.get("category").get("id")).value(categories));
    }

    private Optional<Predicate> getRangeStartPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(filter.getRangeStart())
                .map(rangeStart -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
    }

    private Optional<Predicate> getRangeEndPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(filter.getRangeEnd())
                .map(rangeEnd -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
    }

    private Optional<Predicate> getSearchTextPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(filter.getText())
                .map(text -> criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                ));
    }

    private Optional<Predicate> getPaidPredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        return Optional.ofNullable(filter.getPaid())
                .map(paid -> criteriaBuilder.equal(root.get("paid"), paid));
    }

    private Optional<Predicate> getOnlyAvailablePredicate(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        Subquery<Long> subquery = criteriaBuilder.createQuery().subquery(Long.class);
        Root<ParticipationRequest> subRoot = subquery.from(ParticipationRequest.class);

        subquery.select(criteriaBuilder.count(subRoot.get("id")))
                .where(criteriaBuilder.and(
                        criteriaBuilder.equal(subRoot.get("event"), root),
                        criteriaBuilder.equal(subRoot.get("status"), RequestStatus.CONFIRMED)
                ));

        return Optional.of(criteriaBuilder.lessThan(
                subquery.getSelection(),
                root.get("participantLimit")));
    }

}




