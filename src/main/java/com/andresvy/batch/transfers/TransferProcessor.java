package com.andresvy.batch.transfers;

import org.springframework.batch.item.ItemProcessor;
import java.util.UUID;

public class TransferProcessor implements ItemProcessor<Transfer, Transfer> {

    @Override
    public Transfer process(final Transfer transfer) throws Exception {
        transfer.setId(UUID.randomUUID().toString());

        // Aquí podemos agregar lógica de transformación o validación.
        // ejemplo:
        // if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        //     throw new IllegalArgumentException("El monto debe ser positivo.");
        // }

        System.out.println("Procesando transferencia: " + transfer);
        return transfer;
    }
}
