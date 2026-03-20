package com.bank.antifraud.dto.transfer;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferDto {

    private Long accountTransferId;
    private Long accountNumber;
    private BigDecimal amount;
    private String purpose;

}
