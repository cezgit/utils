package com.wds.util.exceptions;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
