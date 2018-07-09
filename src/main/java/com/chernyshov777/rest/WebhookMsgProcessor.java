package com.chernyshov777.rest;

import com.chernyshov777.data.DestinationRepository;
import com.chernyshov777.data.MessageRepository;
import com.chernyshov777.domain.Destination;
import com.chernyshov777.domain.Message;
import com.chernyshov777.events.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Processor listens for {@link com.chernyshov777.events.MessageReceivedEvent} and sends async message for
 * specify destination.
 */
@Service
public class WebhookMsgProcessor {
    private static final Logger logger = LoggerFactory.getLogger(WebhookMsgProcessor.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    private final RestTemplate restTemplate;

    public WebhookMsgProcessor(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Async EventListener for MessageReceivedEvent
     */
    @Async
    @EventListener
    public void messageReceivedListener(MessageReceivedEvent messageReceivedEvent) {
        Message message = messageReceivedEvent.getMessage();


        processMessagesForDestination(message.getDestination());
    }

    /**
     * Scheduled method to process the messages saved on database
     */
    @Scheduled(cron="0 0 */3 * * *") // Run at minute 0 past every 3th hour ~ 25 times in 3 days
    public void scheduledMessagesProcessor() {

        destinationRepository.findAll().forEach(this::processMessagesForDestination);
    }

    private void processMessagesForDestination(Destination destination) {
        try {

            destinationRepository.setDestinationOnline(destination.getId());

            List<Message> messages = messageRepository.findAllByDestinationOrderByIdAsc(destination);
            for (Message message : messages) {
                if (message.isMessageTimeout()) {
                    deleteMessage(message);
                } else {
                    sendMessage(message);
                }
            }
        } catch (MessageProcessorException ex) {
            logger.info("processMessagesForDestination caught an exception: {}", ex.getMessage());
        }
    }

    private void sendMessage(Message message) throws MessageProcessorException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<Message> request = new HttpEntity<>(message, headers);

            Thread.sleep(500); // wait 0.5 second before send message

            logger.debug("Sending Message {} to Destination {}", message.getId(), message.getDestinationUrl());

            ResponseEntity<String> entity = restTemplate.postForEntity(message.getDestinationUrl(), request, String.class);

            if (entity.getStatusCode().equals(HttpStatus.OK)) {
                onSendMessageSuccess(message);
            } else {
                throw new MessageProcessorException("Non 200 HTTP response code!");
            }
        } catch (Exception ex) {
            logger.info("sendMessage caught an exception: {}", ex.getMessage());

            onSendMessageError(message);
            throw new MessageProcessorException(ex.getMessage());
        }
    }



    private void onSendMessageSuccess(Message message) {
        logger.debug("Sent Message {}", message.getId());

        deleteMessage(message);
    }

    private void onSendMessageError(Message message) {
        logger.debug("Unsent Message {}", message.getId());

        destinationRepository.setDestinationOffline(message.getDestinationId());
    }

    private void deleteMessage(Message message) {
        messageRepository.delete(message);

        logger.debug("Deleted Message {}", message.getId());
    }
}
