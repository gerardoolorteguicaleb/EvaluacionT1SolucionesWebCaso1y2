package pe.edwin.wallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Movement implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-PE"));

    private final MovementType type;
    private final BigDecimal amount;
    private final LocalDateTime createdAt;

    public Movement(MovementType type, BigDecimal amount) {
        this.type = type;
        this.amount = amount.setScale(2);
        this.createdAt = LocalDateTime.now();
    }

    public MovementType getType() {
        return type;
    }

    public String getLabel() {
        return type.getLabel();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return amount.toPlainString();
    }

    public String getFormattedDate() {
        return createdAt.format(DATE_FORMAT);
    }
}
