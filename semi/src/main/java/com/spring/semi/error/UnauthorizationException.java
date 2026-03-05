package com.spring.semi.error;


/**
 * UnauthorizationException - 에러/예외 모델.
 */
public class UnauthorizationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public UnauthorizationException() {
		super();

	}
	public UnauthorizationException(String message) {
		super(message);
	}

	}
