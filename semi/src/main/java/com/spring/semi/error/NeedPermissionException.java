package com.spring.semi.error;


/**
 * NeedPermissionException - 에러/예외 모델.
 */
public class NeedPermissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NeedPermissionException() {
        super();
    }

    public NeedPermissionException(String message) {
        super(message);
    }
}
