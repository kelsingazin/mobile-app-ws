package com.appsdeveloperblog.app.ws.exceptions;

public class AddressServiceException extends RuntimeException{
    private static final long serialVersionUID = 6835192693475021280L;

    public AddressServiceException(String message) {
        super(message);
    }
}
