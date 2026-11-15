package pe.edwin.wallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final BigDecimal INITIAL_BALANCE = new BigDecimal("250.00");
    private BigDecimal balance;
    private final List<Movement> movements = new ArrayList<>();

    public Wallet() {
        this.balance = INITIAL_BALANCE;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getFormattedBalance() {
        return balance.toPlainString();
    }

    public List<Movement> getMovements() {
        return Collections.unmodifiableList(movements);
    }

    public void register(MovementType type, BigDecimal amount) {
        if (type == null || amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Ingresa un monto mayor que cero con hasta dos decimales.");
        }
        BigDecimal normalizedAmount = amount.setScale(2);
        if (type == MovementType.GASTO && normalizedAmount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para registrar este gasto.");
        }
        balance = type == MovementType.RECARGA
                ? balance.add(normalizedAmount)
                : balance.subtract(normalizedAmount);
        movements.add(0, new Movement(type, normalizedAmount));
    }
}
