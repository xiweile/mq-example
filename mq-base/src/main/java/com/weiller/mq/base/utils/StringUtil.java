package com.weiller.mq.base.utils;

import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.*;

public class StringUtil {
	/**
	 * 描述 : <Object转byte[]>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] toByteArray(String obj) throws UnsupportedEncodingException {
		if(obj == null) return null;

		return obj.getBytes(RemotingHelper.DEFAULT_CHARSET);
	}

	/**
	 * 描述 : <byte[]转Object>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToString(byte[] bytes) throws UnsupportedEncodingException {
		if(bytes == null) return null;
		return new String(bytes,RemotingHelper.DEFAULT_CHARSET);
	}
}
