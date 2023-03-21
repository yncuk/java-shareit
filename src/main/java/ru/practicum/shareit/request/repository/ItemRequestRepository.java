package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    Collection<ItemRequest> findAllByRequester_Id(Integer requesterId);

    @Query(" select i from ItemRequest i " +
            "where i.requester.id <> ?1 " +
            "order by i.created")
    Collection<ItemRequest> findAllRequestsExceptRequester(Integer requesterId);
}
