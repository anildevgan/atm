package com.zinkworks.atm.exceptions;

public class BadRequestException extends RuntimeException {
    static final long serialVersionUID = -3387516993334229948L;

    public BadRequestException(String message) {
        super(message);
    }
}
