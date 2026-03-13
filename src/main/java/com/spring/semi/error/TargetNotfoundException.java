package com.spring.semi.error;


/**
 * TargetNotfoundException - 에러/예외 모델.
 */
public class TargetNotfoundException extends RuntimeException{
	private static final long seriralVersionUID= 1L;
	public TargetNotfoundException() {
		super();
	}
	public TargetNotfoundException(String message) {
		super(message);
	}

}
