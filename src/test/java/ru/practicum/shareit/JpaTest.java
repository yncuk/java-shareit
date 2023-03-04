package ru.practicum.shareit;

import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

@AutoConfigureDataJpa
@SpringBootTest(classes = {UserServiceImpl.class, ItemServiceImpl.class},
        properties = {
                "spring.jpa.properties.hibernate.format_sql=true",
                "spring.jpa.show-sql=true",
                "spring.main.allow-circular-references=true"
        })
public abstract class JpaTest {
}
