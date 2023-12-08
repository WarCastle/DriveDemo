package com.castle.drive.exception;

/**
 * @author YuLong
 * @Date 2023/11/9 16:34
 * @Classname UtilException
 * @Description util异常
 */
public class UtilException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UtilException(Exception e){
        super(e);
    }

    public UtilException(String msg){
        super(msg);
    }

    public UtilException(String msg, Exception e){
        super(msg, e);
    }

}
