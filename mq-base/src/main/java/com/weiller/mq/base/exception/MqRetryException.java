package com.weiller.mq.base.exception;

public class MqRetryException extends BizException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3254979735193294815L;

	public MqRetryException(String message) {
		super(message);
	}

	public MqRetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
