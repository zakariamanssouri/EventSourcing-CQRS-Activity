package me.zakaria.comptecqrses.commonapi.exceptions;

public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException(String message) {
        super(message);
    }
}
