package org.zoomdev.zoom.common.exceptions;

public class Exceptions {

    public static RuntimeException runtimeException(Throwable caurse, String format, Object... args) {
        return new RuntimeException(String.format(format, args), caurse);
    }
}
