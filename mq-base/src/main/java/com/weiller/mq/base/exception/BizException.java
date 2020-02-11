package com.weiller.mq.base.exception;

/**
 * 通用异常类
 */
public class BizException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3761565290523287723L;

	public BizException(String message){
		super(message);
	}
	
	public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
