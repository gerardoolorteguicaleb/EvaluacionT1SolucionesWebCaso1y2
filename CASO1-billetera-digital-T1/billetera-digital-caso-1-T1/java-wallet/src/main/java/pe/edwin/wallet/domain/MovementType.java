package pe.edwin.wallet.domain;

public enum MovementType {
    RECARGA("Recarga"),
    GASTO("Gasto");

    private final String label;

    MovementType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
