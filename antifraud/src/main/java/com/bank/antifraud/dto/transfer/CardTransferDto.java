package com.bank.antifraud.dto.transfer;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTransferDto {

    private Long cardTransferId;
    private Long cardNumber;
    private BigDecimal amount;
    private String purpose;

}
