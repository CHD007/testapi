package com.chernyshov777.rest;

import com.chernyshov777.domain.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple rest controller for testing async notification
 */
@RestController
public class MsgHandler {


    @PostMapping(value = "/msg/receive")
    public String msgReceiver(@RequestBody Message body) {
        System.out.println("Received msg " + body);
        return "ok";
    }
}
