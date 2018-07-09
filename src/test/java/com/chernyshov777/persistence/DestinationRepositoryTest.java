package com.chernyshov777.persistence;

import com.chernyshov777.domain.Destination;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DestinationRepositoryTest extends AbstractRepositoryTest {
    /**
     * Test the DestinationRepository.DestinationOffline() method.
     */
    @Test
    public void setDestinationOnlineOfflineTest() {
        logger.debug("setDestinationOnlineTest");

        // Retrieve trelloDest to check the online flag
        Optional<Destination> result = destinationRepository.findById(googleDest.getId());
        assertTrue(result.isPresent());
        assertThat(result.get().isOnline()).isFalse();

        destinationRepository.setDestinationOnline(googleDest.getId());

    }

    /**
     * Test the relation between Destination and Message
     */
    @Test
    public void deleteDestinationCorrectly() {
        logger.debug("deleteDestinationCorrectly");

        destinationRepository.delete(googleDest);

        // Tries to find googleDest to ensure it was deleted
        Optional<Destination> result = destinationRepository.findById(googleDest.getId());
        assertFalse(result.isPresent());
    }
}
