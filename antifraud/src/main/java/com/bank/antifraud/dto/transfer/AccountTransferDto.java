package com.bank.antifraud.dto.transfer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
