package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select distinct i from Item i " +
            "left join fetch i.comments " +
            "where i.owner = ?1 " +
            "order by i.id")
    List<Item> findItemsByOwner(Integer userId);

    @Query(" select i from Item i " +
            "where (lower(i.name) like lower(concat('%',?1,'%')) or " +
            "lower(i.description) like lower(concat('%',?1,'%'))) and " +
            "i.available = true ")
    List<Item> search(String text);

    @Query("select distinct i from Item i " +
            "left join fetch i.comments " +
            "where i.id = ?1")
    Optional<Item> findByIdFetch(Integer itemId);


    //List<Item> findItemsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIs(String text, String text2, Boolean available);
}
