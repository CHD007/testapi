package com.chernyshov777.data;

import com.chernyshov777.domain.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    @Modifying
    @Transactional
    @Query("update Destination d set d.online = true where d.id = ?1")
    int setDestinationOnline(Long id);

    @Modifying
    @Transactional
    @Query("update Destination d set d.online = false where d.id = ?1")
    int setDestinationOffline(Long id);
}
