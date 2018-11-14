package org.zoomdev.zoom.common.exceptions;

/**
 *
 * 这个异常用来判断是否是数据校验错误
 *
 */
public class ValidateException extends ZoomException {


    public ValidateException(String message){
        super(message);
    }

}
