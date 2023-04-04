package shareit;

import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.config.DbConfig;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

@AutoConfigureDataJpa
@SpringBootTest(classes = {DbConfig.class, UserServiceImpl.class, ItemServiceImpl.class, ItemRequestServiceImpl.class, BookingServiceImpl.class},
        properties = {
                "spring.jpa.properties.hibernate.format_sql=true",
                "spring.jpa.show-sql=true",
                "spring.main.allow-circular-references=true"
        })
public abstract class JpaTest {
}
