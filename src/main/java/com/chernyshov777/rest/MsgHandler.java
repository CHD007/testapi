package com.chernyshov777.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MsgHandler {


    @PostMapping(value = "/msg/receive")
    public String msgReceiver(@RequestBody String body) {
        System.out.println("Received msg " + body);
        return "ok";
    }
}
