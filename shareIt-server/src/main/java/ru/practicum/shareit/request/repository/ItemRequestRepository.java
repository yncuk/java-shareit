package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("select i from ItemRequest i " +
            "left join fetch i.items " +
            "where i.requester.id = ?1")
    Collection<ItemRequest> findAllByRequesterFetch(Integer requesterId);

    @Query(" select i from ItemRequest i " +
            "left join fetch i.items " +
            "where i.requester.id <> ?1 " +
            "order by i.created")
    Collection<ItemRequest> findAllRequestsExceptRequester(Integer requesterId);

    @Query(" select i from ItemRequest i " +
            "left join fetch i.items " +
            "where i.id = ?1")
    Optional<ItemRequest> findByIdFetch(Integer requestId);
}
