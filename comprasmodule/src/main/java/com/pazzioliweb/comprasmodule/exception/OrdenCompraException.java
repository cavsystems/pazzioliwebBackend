package com.pazzioliweb.comprasmodule.exception;

public class OrdenCompraException extends RuntimeException {
    
    public OrdenCompraException() {
        super();
    }
    
    public OrdenCompraException(String message) {
        super(message);
    }
    
    public OrdenCompraException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OrdenCompraException(Throwable cause) {
        super(cause);
    }
}
