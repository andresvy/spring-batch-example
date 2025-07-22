package com.andresvy.batch.transfers;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transfer {

    @Id
    private String id;
    private String sourceBankCode;
    private String sourceAccountNumber;
    private String destinationBankCode;
    private String destinationAccountNumber;
    private BigDecimal amount;

    public Transfer(String sourceBankCode, String sourceAccountNumber,
                    String destinationBankCode, String destinationAccountNumber, BigDecimal amount) {
        this.sourceBankCode = sourceBankCode;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationBankCode = destinationBankCode;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
    }
}
