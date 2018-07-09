package com.chernyshov777.persistence;

import com.chernyshov777.Application;
import com.chernyshov777.data.DestinationRepository;
import com.chernyshov777.data.MessageRepository;
import com.chernyshov777.domain.Destination;
import com.chernyshov777.domain.Message;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class})
public abstract class AbstractRepositoryTest {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    DestinationRepository destinationRepository;

    @Autowired
    MessageRepository messageRepository;

    Destination googleDest;

    Message googleMessage;

    @Before
    public void setUp() {
        logger.debug("setUp");

        messageRepository.deleteAll();
        destinationRepository.deleteAll();

        googleDest = new Destination("http://www.google.com");

        Destination savedDestination = destinationRepository.save(googleDest);

        googleMessage = new Message("#safe=off&q=hootsuite", "text/html", savedDestination);

        messageRepository.save(googleMessage);
    }

}
