package com.chernyshov777.data;

import com.chernyshov777.domain.Destination;
import com.chernyshov777.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByDestinationOrderByIdAsc(Destination destination);
}
