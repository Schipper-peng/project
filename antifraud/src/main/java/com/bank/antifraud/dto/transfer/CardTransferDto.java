package com.bank.antifraud.dto.transfer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
