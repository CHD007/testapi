package com.chernyshov777.rest;

import com.chernyshov777.data.DestinationRepository;
import com.chernyshov777.data.MessageRepository;
import com.chernyshov777.domain.*;
import com.chernyshov777.events.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

@RestController
public class PaymentController implements ApplicationEventPublisherAware {

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private MessageRepository messageRepository;

    private ApplicationEventPublisher applicationEventPublisher;

    @PreAuthorize("#oauth2.hasScope('payments/.*')")
    @RequestMapping(method = RequestMethod.POST, value = "/payments/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> payment(@Valid @RequestBody Payment payment, Errors errors) {
        /*Mimics payment creation logic*/
        Random idGenerator = new Random();
        String generatedId = Integer.toString(Math.abs(idGenerator.nextInt()));
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(generatedId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        paymentResponse.setCreateTime(sdf.format(new Date()));

        if (errors.hasErrors()) {
            paymentResponse.setState(State.FAILED);
            return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
        }
        paymentResponse.setState(State.CREATED);

        createMessageToSendItLatter(payment, paymentResponse);

        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    /**
     * Save destination entity based on payment destination parameter.
     * Save message and publish event about new message creation.
     *
     * @param payment payment which used to create destination and message
     */
    private void createMessageToSendItLatter(Payment payment, PaymentResponse paymentResponse) {
        Destination destination = destinationRepository.save(new Destination(payment.getNotificationUrl()));
        Message message = new Message(MediaType.APPLICATION_JSON_VALUE, destination);
        message.setAmount(payment.getTransaction().getAmount().getValue());
        message.setContentType(MediaType.APPLICATION_JSON_VALUE);
        message.setCurrency(payment.getTransaction().getAmount().getCurrency());
        message.setSha2("generatedSha2");
        message.setPaymentId(paymentResponse.getId());
        message.setExternalId(payment.getTransaction().getExternalId());
        message.setStatus(paymentResponse.getState());
        Message simpleMsg = messageRepository.save(message);
        applicationEventPublisher.publishEvent(new MessageReceivedEvent(this, simpleMsg));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
