package com.chernyshov777.rest;

import com.chernyshov777.domain.Payment;
import com.chernyshov777.domain.PaymentResponse;
import com.chernyshov777.domain.State;
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
public class PaymentController {

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
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
}
