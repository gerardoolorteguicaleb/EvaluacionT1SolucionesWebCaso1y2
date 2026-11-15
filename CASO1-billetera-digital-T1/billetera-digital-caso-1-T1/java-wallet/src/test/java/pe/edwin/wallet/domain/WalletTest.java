package pe.edwin.wallet.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {
    @Test
    void startsAt250SolesWithEmptyHistory() {
        Wallet wallet = new Wallet();

        assertEquals(new BigDecimal("250.00"), wallet.getBalance());
        assertEquals(0, wallet.getMovements().size());
    }

    @Test
    void addsRechargeAndSubtractsExpenseAndPrependsNewestMovement() {
        Wallet wallet = new Wallet();

        wallet.register(MovementType.RECARGA, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("350.00"), wallet.getBalance());

        wallet.register(MovementType.GASTO, new BigDecimal("50.00"));
        assertEquals(new BigDecimal("300.00"), wallet.getBalance());
        assertEquals(MovementType.GASTO, wallet.getMovements().get(0).getType());
        assertEquals(new BigDecimal("50.00"), wallet.getMovements().get(0).getAmount());
        assertEquals(MovementType.RECARGA, wallet.getMovements().get(1).getType());
    }

    @Test
    void permitsSpendingExactlyTheAvailableBalance() {
        Wallet wallet = new Wallet();

        wallet.register(MovementType.GASTO, new BigDecimal("250.00"));

        assertEquals(new BigDecimal("0.00"), wallet.getBalance());
        assertEquals(1, wallet.getMovements().size());
    }

    @Test
    void rejectsSpendingMoreThanTheAvailable250SolesWithoutChangingTheWallet() {
        Wallet wallet = new Wallet();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wallet.register(MovementType.GASTO, new BigDecimal("400.00")));

        assertEquals("Saldo insuficiente para registrar este gasto.", exception.getMessage());
        assertEquals(new BigDecimal("250.00"), wallet.getBalance());
        assertEquals(0, wallet.getMovements().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.00", "-0.01", "100.001"})
    void rejectsZeroNegativeAndMoreThanTwoDecimalAmountsWithoutChangingBalance(String invalid) {
        Wallet wallet = new Wallet();

        assertThrows(IllegalArgumentException.class,
                () -> wallet.register(MovementType.RECARGA, new BigDecimal(invalid)));
        assertEquals(new BigDecimal("250.00"), wallet.getBalance());
        assertEquals(0, wallet.getMovements().size());
    }
}
