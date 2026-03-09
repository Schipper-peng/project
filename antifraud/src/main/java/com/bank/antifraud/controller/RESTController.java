package com.bank.antifraud.controller;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.service.SuspiciousTransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/transfer")
//public class RESTController {
//    private final SuspiciousTransferService service;
//
//    public RESTController(SuspiciousTransferService service) {
//        this.service = service;
//    }
//    @PutMapping("/phone")
//    public ResponseEntity<Void> putPhone(@RequestBody SuspiciousPhoneTransferDto dto) {
//        service.upsertPhone(dto);
//        return ResponseEntity.ok().build();
//    }
//    @PutMapping("/account")
//    public ResponseEntity<Void> putAccount(@RequestBody SuspiciousAccountTransferDto dto) {
//        service.upsertAccount(dto);
//        return ResponseEntity.ok().build();
//    }
//    @PutMapping("/card")
//    public ResponseEntity<Void> putCard(@RequestBody SuspiciousCardTransferDto dto) {
//        System.out.println("DTO transferId = " + dto.getCardTransferId());
//        service.upsertCard(dto);
//        return ResponseEntity.ok().build();
//    }
//}
