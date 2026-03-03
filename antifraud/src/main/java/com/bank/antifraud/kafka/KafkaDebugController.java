package com.bank.antifraud.kafka;

import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.producer.SuspiciousTransferProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/debug/kafka")
//public class KafkaDebugController {
//    private final SuspiciousTransferProducer producer;
//
//    @PostMapping("/create")
//    public void send(@RequestBody SuspiciousTransferCommand command) {
//        producer.send(command);
//    }
//}
