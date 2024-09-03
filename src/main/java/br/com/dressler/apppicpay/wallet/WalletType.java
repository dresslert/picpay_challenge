package br.com.dressler.apppicpay.wallet;

public enum WalletType {
    COMUM(1), LOJISTA(2);

    private final int value;  // Tornar 'value' final

    private WalletType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
